package com.example.prcticacalificada2.ui.imagenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.prcticacalificada2.DiarioEspacialApp
import com.example.prcticacalificada2.data.local.ApodEntity
import com.example.prcticacalificada2.data.repository.ApodRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

private fun CreationExtras.apodRepository(): ApodRepository =
    (this[APPLICATION_KEY] as DiarioEspacialApp).container.apodRepository

// ---------------------------------------------------------------- Lista

data class ApodListUiState(
    val apods: List<ApodEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ApodListViewModel(private val repository: ApodRepository) : ViewModel() {

    private val isLoading = MutableStateFlow(true)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ApodListUiState> =
        combine(repository.observeLastWeek(), isLoading, errorMessage) { list, loading, error ->
            ApodListUiState(apods = list, isLoading = loading, errorMessage = error)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ApodListUiState(isLoading = true))

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                repository.refreshApods()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorMessage.value = e.toUserMessage()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun toggleFavorite(apod: ApodEntity) {
        viewModelScope.launch { repository.setFavorite(apod.date, !apod.isFavorite) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { ApodListViewModel(apodRepository()) }
        }
    }
}

// ---------------------------------------------------------------- Detalle

data class ApodDetailUiState(
    val isLoading: Boolean = true,
    val apod: ApodEntity? = null
)

class ApodDetailViewModel(private val repository: ApodRepository, date: String) : ViewModel() {

    val uiState: StateFlow<ApodDetailUiState> =
        repository.observeApod(date)
            .map { ApodDetailUiState(isLoading = false, apod = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ApodDetailUiState())

    fun toggleFavorite() {
        val current = uiState.value.apod ?: return
        viewModelScope.launch { repository.setFavorite(current.date, !current.isFavorite) }
    }

    companion object {
        const val ARG_DATE = "date"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val date = createSavedStateHandle().get<String>(ARG_DATE).orEmpty()
                ApodDetailViewModel(apodRepository(), date)
            }
        }
    }
}

// ---------------------------------------------------------------- Favoritos

data class FavoriteApodsUiState(
    val isLoading: Boolean = true,
    val apods: List<ApodEntity> = emptyList()
)

class FavoriteApodsViewModel(private val repository: ApodRepository) : ViewModel() {

    val uiState: StateFlow<FavoriteApodsUiState> =
        repository.observeFavorites()
            .map { FavoriteApodsUiState(isLoading = false, apods = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavoriteApodsUiState())

    fun removeFavorite(apod: ApodEntity) {
        viewModelScope.launch { repository.setFavorite(apod.date, false) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { FavoriteApodsViewModel(apodRepository()) }
        }
    }
}

private fun Exception.toUserMessage(): String = when (this) {
    is HttpException -> when (code()) {
        401, 403 -> "La clave de la API no es válida. Revisa NASA_API_KEY en local.properties."
        429 -> "Se superó el límite de consultas de la API. Intenta más tarde."
        else -> "Error del servidor de la NASA (${code()})."
    }
    is IOException -> "No se pudo conectar. Revisa tu conexión a internet."
    is IllegalStateException -> message ?: "Error al consultar las imágenes."
    else -> "Error al consultar las imágenes."
}
