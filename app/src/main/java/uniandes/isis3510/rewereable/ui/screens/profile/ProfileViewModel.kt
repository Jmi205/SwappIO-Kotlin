package uniandes.isis3510.rewereable.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.model.User
import uniandes.isis3510.rewereable.domain.repository.AuthRepository
import uniandes.isis3510.rewereable.domain.repository.UserRepository

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUserProfile()
    }

    private fun loadCurrentUserProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            val currentUserId = auth.currentUser?.uid

            if (currentUserId != null) {
                val result = userRepository.getUserProfile(currentUserId)

                result.fold(
                    onSuccess = { user ->
                        _uiState.value = ProfileUiState.Success(user)
                    },
                    onFailure = { error ->
                        _uiState.value = ProfileUiState.Error(error.message ?: "Error desconocido")
                    }
                )
            } else {
                _uiState.value = ProfileUiState.Error("No hay un usuario autenticado.")
            }
        }
    }




    fun onWithdrawClicked() {
    }

    fun onLogoutClicked() {
        authRepository.logout()
    }

    companion object {
        fun provideFactory(
            userRepo: UserRepository,
            authRepo: AuthRepository // Agregamos al Factory
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(userRepo, authRepo) as T
            }
        }
    }
}