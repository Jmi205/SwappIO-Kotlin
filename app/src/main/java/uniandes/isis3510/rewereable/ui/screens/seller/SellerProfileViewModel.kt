package uniandes.isis3510.rewereable.ui.screens.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.model.Product
import uniandes.isis3510.rewereable.domain.model.User
import uniandes.isis3510.rewereable.domain.repository.ProductRepository
import uniandes.isis3510.rewereable.domain.repository.UserRepository
import uniandes.isis3510.rewereable.ui.screens.home.HomeUiState

sealed class SellerProfileUiState {
    object Loading : SellerProfileUiState()
    data class Success(
        val seller: User,
        val activeProducts: List<Product>,
        val isFollowing: Boolean,
        val currentUserId: String?,
        val favoriteIds: Set<String> = emptySet()
    ) : SellerProfileUiState()
    data class Error(val message: String) : SellerProfileUiState()
}

class SellerProfileViewModel(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val sellerId: String
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow<SellerProfileUiState>(SellerProfileUiState.Loading)
    val uiState: StateFlow<SellerProfileUiState> = _uiState.asStateFlow()

    init {
        loadSellerData()
    }

    private fun loadSellerData() {
        viewModelScope.launch {
            _uiState.value = SellerProfileUiState.Loading

            val currentUserId = auth.currentUser?.uid
            val sellerResult = userRepository.getUserProfile(sellerId)

            if (sellerResult.isSuccess && currentUserId != null) {

                val userResult = userRepository.getUserProfile(currentUserId)
                val currentUser = userResult.getOrNull()!!
                val seller = sellerResult.getOrNull()!!
                val isFollowing = seller.followers.contains(currentUserId)

                val products = seller.listings.mapNotNull { productId ->
                    productRepository.getProductById(productId).getOrNull()
                }.filter { it.status.name == "AVAILABLE" }

                val initialFavorites = currentUser.favorites.toSet()





                _uiState.value = SellerProfileUiState.Success(
                    seller = seller,
                    activeProducts = products,
                    isFollowing = isFollowing,
                    currentUserId = currentUserId,
                    favoriteIds = initialFavorites
                )
            } else {
                _uiState.value = SellerProfileUiState.Error("Error al cargar el perfil del vendedor")
            }
        }
    }

    fun toggleFollow() {
        val currentState = _uiState.value
        if (currentState is SellerProfileUiState.Success) {
            val currentUserId = currentState.currentUserId ?: return

            val wasFollowing = currentState.isFollowing

            _uiState.value = currentState.copy(isFollowing = !wasFollowing)

            viewModelScope.launch {
                val result = if (wasFollowing) {
                    userRepository.unfollowUser(currentUserId = currentUserId, sellerId = sellerId)
                } else {
                    userRepository.followUser(currentUserId = currentUserId, sellerId = sellerId)
                }

                if (result.isFailure) {
                    _uiState.value = currentState.copy(isFollowing = wasFollowing)
                }
            }
        }
    }

    fun toggleFavorite(productId: String) {
        val currentState = _uiState.value
        if (currentState is SellerProfileUiState.Success) {
            val currentFavs = currentState.favoriteIds.toMutableSet()

            val userId = currentState.currentUserId ?: return

            viewModelScope.launch {
                if (currentFavs.contains(productId)) {
                    currentFavs.remove(productId)
                    userRepository.removeFromFavorites(userId, productId)
                } else {
                    currentFavs.add(productId)
                    userRepository.addToFavorites(userId, productId)
                }
                // Actualizamos la UI
                _uiState.value = currentState.copy(favoriteIds = currentFavs)
            }
        }
    }

    companion object {
        fun provideFactory(
            userRepository: UserRepository,
            productRepository: ProductRepository,
            sellerId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SellerProfileViewModel(userRepository, productRepository, sellerId) as T
            }
        }
    }
}