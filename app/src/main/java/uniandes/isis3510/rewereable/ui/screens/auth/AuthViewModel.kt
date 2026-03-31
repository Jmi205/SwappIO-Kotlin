package uniandes.isis3510.rewereable.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.repository.AuthRepository

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(email, pass)

            result.fold(
                onSuccess = { _uiState.value = AuthUiState.Success },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Error al iniciar sesión") }
            )
        }
    }

    fun register(email: String, pass: String, name: String, lastname: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(email, pass, name, lastname)

            result.fold(
                onSuccess = { _uiState.value = AuthUiState.Success },
                onFailure = { _uiState.value = AuthUiState.Error(it.message ?: "Error al registrar") }
            )
        }
    }
    //Factory?
}