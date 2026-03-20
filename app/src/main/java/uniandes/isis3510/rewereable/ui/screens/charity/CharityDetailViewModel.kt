package uniandes.isis3510.rewereable.ui.screens.charity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.model.Charity
import uniandes.isis3510.rewereable.domain.repository.CharityRepository

sealed class CharityDetailUiState {
    object Loading : CharityDetailUiState()
    data class Success(val charity: Charity) : CharityDetailUiState()
    data class Error(val message: String) : CharityDetailUiState()
}

class CharityDetailViewModel(
    private val charityRepository: CharityRepository,
    private val charityId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharityDetailUiState>(CharityDetailUiState.Loading)
    val uiState: StateFlow<CharityDetailUiState> = _uiState.asStateFlow()

    init {
        loadCharity()
    }

    private fun loadCharity() {
        viewModelScope.launch {
            _uiState.value = CharityDetailUiState.Loading

            val result = charityRepository.getCharityById(charityId)

            _uiState.value = if (result.isSuccess) {
                CharityDetailUiState.Success(result.getOrThrow())
            } else {
                CharityDetailUiState.Error("No se pudo cargar la charity.")
            }
        }
    }

    companion object {
        fun provideFactory(
            charityRepository: CharityRepository,
            charityId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CharityDetailViewModel(charityRepository, charityId) as T
            }
        }
    }
}