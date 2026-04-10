package uniandes.isis3510.rewereable.ui.screens.product

import android.util.Log
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
import uniandes.isis3510.rewereable.domain.repository.ChatRepository
import uniandes.isis3510.rewereable.domain.repository.ProductRepository
import uniandes.isis3510.rewereable.domain.repository.UserRepository

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(
        val product: Product,
        val owner: User,
        val currentUserId: String?,
        val suggestions: List<Product>
    ) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val productId: String
) : ViewModel() {


    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            val productResult = productRepository.getProductById(productId)
            val currentUserId = auth.currentUser?.uid

            if (productResult.isSuccess) {
                val product = productResult.getOrNull()!!
                val ownerResult = userRepository.getUserProfile(product.ownerId)

                val suggestionsResult = productRepository.getTrendingProducts()
                val suggestions = suggestionsResult.getOrDefault(emptyList())
                    .filter { it.id != productId }
                    .take(5)
                val owner = if (ownerResult.isSuccess) {
                    ownerResult.getOrNull()!!
                } else {
                    User(
                        id = product.ownerId, name = "Usuario", lastname = "Desconocido",
                        email = "", balance = 0.0, location = "", memberSince = 0L, profilePictureUrl = null, number = ""
                    )
                }

                _uiState.value = DetailUiState.Success(product, owner, currentUserId, suggestions)
            } else {
                _uiState.value = DetailUiState.Error("Error al cargar el producto")
            }
        }
    }

    fun deleteProduct(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            val currentUserId = currentState.currentUserId ?: return

            viewModelScope.launch {
                _uiState.value = DetailUiState.Loading

                val result = productRepository.deleteProduct(productId, currentUserId)

                if (result.isSuccess) {
                    onSuccess()
                } else {
                    _uiState.value = DetailUiState.Error("No se pudo borrar el producto. Revisa tu conexión.")
                }
            }
        }
    }

    fun startChatWithSeller(onChatCreated: (String) -> Unit) {
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            val currentUserId = currentState.currentUserId ?: return
            val seller = currentState.owner
            val product = currentState.product

            if (currentUserId == seller.id) return

            viewModelScope.launch {
                val currentUserResult = userRepository.getUserProfile(currentUserId)
                val currentUser = currentUserResult.getOrNull()
                val currentUserName = currentUser?.let { "${it.name} ${it.lastname}" } ?: "Comprador"
                val sellerFullName = "${seller.name} ${seller.lastname}"

                val result = chatRepository.createChatChannel(
                    currentUserId = currentUserId,
                    sellerId = seller.id,
                    currentUserName = currentUserName,
                    sellerName = sellerFullName,
                    currentUserPic = currentUser?.profilePictureUrl,
                    sellerPic = seller.profilePictureUrl,
                    productId = product.id,
                    productName = product.name,
                    productPrice = product.price,
                    productImage = product.images.firstOrNull()
                )

                if (result.isSuccess) {
                    onChatCreated(result.getOrNull()!!)
                } else {
                    _uiState.value = DetailUiState.Error("No se pudo iniciar el chat.")
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            productRepository: ProductRepository,
            userRepository: UserRepository,
            chatRepository: ChatRepository, // ¡NUEVO!
            productId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductDetailViewModel(productRepository, userRepository, chatRepository, productId) as T
            }
        }
    }
}