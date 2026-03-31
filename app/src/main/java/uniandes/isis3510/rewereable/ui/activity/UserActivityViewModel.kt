package uniandes.isis3510.rewereable.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.model.Product
import uniandes.isis3510.rewereable.domain.repository.ProductRepository
import uniandes.isis3510.rewereable.domain.repository.UserRepository

enum class ActivityType { FAVORITES, LISTINGS, PURCHASES }

sealed class UserActivityUiState {
    object Loading : UserActivityUiState()
    data class Success(val products: List<Product>, val favoriteIds: Set<String>) : UserActivityUiState()
    data class Error(val message: String) : UserActivityUiState()
    object Empty : UserActivityUiState()
}

class UserActivityViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val activityType: ActivityType
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow<UserActivityUiState>(UserActivityUiState.Loading)
    val uiState: StateFlow<UserActivityUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = UserActivityUiState.Loading
            val currentUserId = auth.currentUser?.uid

            if (currentUserId != null) {
                val userResult = userRepository.getUserProfile(currentUserId)

                if (userResult.isSuccess) {
                    val user = userResult.getOrNull()!!
                    val favoriteIdsSet = user.favorites.toSet()

                    val targetIds = when (activityType) {
                        ActivityType.FAVORITES -> user.favorites
                        ActivityType.LISTINGS -> user.listings
                        ActivityType.PURCHASES -> user.purchases
                    }

                    if (targetIds.isEmpty()) {
                        _uiState.value = UserActivityUiState.Empty
                    } else {
                        val products = targetIds.mapNotNull { id ->
                            productRepository.getProductById(id).getOrNull()
                        }

                        _uiState.value = UserActivityUiState.Success(products, favoriteIdsSet)
                    }
                } else {
                    _uiState.value = UserActivityUiState.Error("Error al cargar el perfil")
                }
            } else {
                _uiState.value = UserActivityUiState.Error("Usuario no autenticado")
            }
        }
    }

    companion object {
        fun provideFactory(
            userRepository: UserRepository,
            productRepository: ProductRepository,
            activityType: ActivityType
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserActivityViewModel(userRepository, productRepository, activityType) as T
            }
        }
    }
}