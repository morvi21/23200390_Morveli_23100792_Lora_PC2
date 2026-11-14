package pe.edu.demo.diarioespacial

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SpaceViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = SpaceRepository(SpaceDatabase.get(app))
    val apods: StateFlow<List<ApodEntity>> = repo.apods.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )
    val favoriteApods: StateFlow<List<ApodEntity>> = repo.favoriteApods.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )
    private val _imageLoading = MutableStateFlow(false)
    val imageLoading = _imageLoading.asStateFlow()
    private val _imageError = MutableStateFlow<String?>(null)
    val imageError = _imageError.asStateFlow()
    private var imageRequested = false

    fun apod(date: String) = repo.apod(date)

    fun loadImages(force: Boolean = false) {
        if (imageRequested && !force) return
        if (_imageLoading.value) return
        imageRequested = true
        viewModelScope.launch {
            _imageLoading.value = true
            _imageError.value = null
            try {
                check(BuildConfig.NASA_API_KEY.isNotBlank()) {
                    "Configura NASA_API_KEY en local.properties y recompila."
                }
                repo.refreshApods()
            } catch (e: Exception) {
                _imageError.value = readable(e)
            } finally { _imageLoading.value = false }
        }
    }

    fun toggleImage(date: String) = viewModelScope.launch { repo.toggleApod(date) }

    private fun readable(e: Exception): String = when (e) {
        is HttpException -> "NASA respondió HTTP ${e.code()}. Verifica clave, fechas o límite de consultas."
        is IOException -> "Sin conexión o servicio no disponible. Se mantienen datos locales."
        else -> e.message ?: "No se pudieron actualizar los datos."
    }
}
