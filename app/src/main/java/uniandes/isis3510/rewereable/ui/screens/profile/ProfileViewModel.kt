package uniandes.isis3510.rewereable.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import uniandes.isis3510.rewereable.domain.model.User
import uniandes.isis3510.rewereable.domain.repository.UserRepository

sealed class ProfileUiState{
    object Loading: ProfileUiState()
    data class Success(val user: User): ProfileUiState()
    data class Error(val message: String): ProfileUiState()
}

class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)

    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {

        loadUserProfile("camila_123")
    }

    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading // Avisamos que estamos cargando

            val result = userRepository.getUserProfile(userId)

            result.fold(
                onSuccess = { user ->
                    _uiState.value = ProfileUiState.Success(user)
                },
                onFailure = { error ->
                    _uiState.value = ProfileUiState.Error(error.message ?: "Error desconocido")
                }
            )
        }
    }

    fun onWithdrawClicked() {

    }
}
