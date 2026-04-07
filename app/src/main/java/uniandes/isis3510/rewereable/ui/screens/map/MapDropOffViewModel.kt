package uniandes.isis3510.rewereable.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.model.DropOffPoint
import uniandes.isis3510.rewereable.domain.repository.DropOffRepository

sealed class MapDropOffUiState {
    object Loading : MapDropOffUiState()

    data class Success(
        val userLatitude: Double,
        val userLongitude: Double,
        val points: List<DropOffPoint>,
        val selectedPointId: String? = null
    ) : MapDropOffUiState()

    data class Error(val message: String) : MapDropOffUiState()
}

class MapDropOffViewModel(
    private val dropOffRepository: DropOffRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapDropOffUiState>(MapDropOffUiState.Loading)
    val uiState: StateFlow<MapDropOffUiState> = _uiState.asStateFlow()

    private val defaultUserLat = 4.6097
    private val defaultUserLng = -74.0817

    init {
        loadDropOffPoints()
    }

    private fun loadDropOffPoints() {
        viewModelScope.launch {
            _uiState.value = MapDropOffUiState.Loading

            val result = dropOffRepository.getNearbyDropOffPoints(defaultUserLat, defaultUserLng)

            _uiState.value = if (result.isSuccess) {
                val points = result.getOrDefault(emptyList())
                MapDropOffUiState.Success(
                    userLatitude = defaultUserLat,
                    userLongitude = defaultUserLng,
                    points = points,
                    selectedPointId = points.firstOrNull()?.id
                )
            } else {
                MapDropOffUiState.Error("No se pudieron cargar los puntos de entrega.")
            }
        }
    }

    fun selectDropOff(pointId: String) {
        val currentState = _uiState.value
        if (currentState is MapDropOffUiState.Success) {
            _uiState.value = currentState.copy(selectedPointId = pointId)
        }
    }

    companion object {
        fun provideFactory(
            dropOffRepository: DropOffRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapDropOffViewModel(dropOffRepository) as T
            }
        }
    }
}