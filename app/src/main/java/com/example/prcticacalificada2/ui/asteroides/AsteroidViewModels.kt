package com.example.prcticacalificada2.ui.asteroides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.prcticacalificada2.DiarioEspacialApp
import com.example.prcticacalificada2.data.local.AsteroidEntity
import com.example.prcticacalificada2.data.repository.AsteroidRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/** Obtiene el repositorio desde la clase Application. */
private fun CreationExtras.asteroidRepository(): AsteroidRepository =
    (this[APPLICATION_KEY] as DiarioEspacialApp).container.asteroidRepository

// ---------------------------------------------------------------- Lista

data class AsteroidListUiState(
    val asteroids: List<AsteroidEntity> = emptyList(),
    val isLoading: Boolean = false,
    val onlyHazardous: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class AsteroidListViewModel(private val repository: AsteroidRepository) : ViewModel() {

    private val onlyHazardous = MutableStateFlow(false)
    private val isLoading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)

    // La lista SIEMPRE se lee desde Room.
    private val asteroids = onlyHazardous.flatMapLatest { repository.observeAsteroids(it) }

    val uiState: StateFlow<AsteroidListUiState> =
        combine(asteroids, onlyHazardous, isLoading, errorMessage) { list, hazardous, loading, error ->
            AsteroidListUiState(
                asteroids = list,
                isLoading = loading,
                onlyHazardous = hazardous,
                errorMessage = error
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AsteroidListUiState(isLoading = true)
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                repository.refreshFeed()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorMessage.value = e.toUserMessage()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun setOnlyHazardous(value: Boolean) {
        onlyHazardous.value = value
    }

    fun toggleFavorite(asteroid: AsteroidEntity) {
        viewModelScope.launch { repository.setFavorite(asteroid.id, !asteroid.isFavorite) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { AsteroidListViewModel(asteroidRepository()) }
        }
    }
}

// ---------------------------------------------------------------- Detalle

data class AsteroidDetailUiState(
    val isLoading: Boolean = true,
    val asteroid: AsteroidEntity? = null
)

class AsteroidDetailViewModel(
    private val repository: AsteroidRepository,
    asteroidId: String
) : ViewModel() {

    val uiState: StateFlow<AsteroidDetailUiState> =
        repository.observeAsteroid(asteroidId)
            .map { AsteroidDetailUiState(isLoading = false, asteroid = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AsteroidDetailUiState())

    fun toggleFavorite() {
        val current = uiState.value.asteroid ?: return
        viewModelScope.launch { repository.setFavorite(current.id, !current.isFavorite) }
    }

    companion object {
        const val ARG_ID = "id"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val id = createSavedStateHandle().get<String>(ARG_ID).orEmpty()
                AsteroidDetailViewModel(asteroidRepository(), id)
            }
        }
    }
}

// ---------------------------------------------------------------- Favoritos

data class FavoriteAsteroidsUiState(
    val isLoading: Boolean = true,
    val asteroids: List<AsteroidEntity> = emptyList()
)

class FavoriteAsteroidsViewModel(private val repository: AsteroidRepository) : ViewModel() {

    val uiState: StateFlow<FavoriteAsteroidsUiState> =
        repository.observeFavorites()
            .map { FavoriteAsteroidsUiState(isLoading = false, asteroids = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoriteAsteroidsUiState())

    fun removeFavorite(asteroid: AsteroidEntity) {
        viewModelScope.launch { repository.setFavorite(asteroid.id, false) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { FavoriteAsteroidsViewModel(asteroidRepository()) }
        }
    }
}

// ---------------------------------------------------------------- Errores

private fun Exception.toUserMessage(): String = when (this) {
    is HttpException -> when (code()) {
        401, 403 -> "La clave de la API no es válida. Revisa NASA_API_KEY en local.properties."
        429 -> "Se superó el límite de consultas de la API. Intenta más tarde."
        else -> "Error del servidor de la NASA (${code()})."
    }
    is IOException -> "No se pudo conectar. Revisa tu conexión a internet."
    is IllegalStateException -> message ?: "Error al consultar los asteroides."
    else -> "Error al consultar los asteroides."
}
