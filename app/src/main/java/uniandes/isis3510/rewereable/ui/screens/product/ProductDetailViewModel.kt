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
            val currentUserId = auth.currentUser?.uid // ¡NUEVO!

            if (productResult.isSuccess) {
                val product = productResult.getOrNull()!!
                val ownerResult = userRepository.getUserProfile(product.ownerId)

                // ¡NUEVO! Cargamos sugerencias (ej. productos trending, excluyendo el actual)
                val suggestionsResult = productRepository.getTrendingProducts()
                val suggestions = suggestionsResult.getOrDefault(emptyList())
                    .filter { it.id != productId }
                    .take(5) // Tomamos solo 5 sugerencias

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
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            val result = productRepository.deleteProduct(productId)

            if (result.isSuccess) {
                onSuccess()
            } else {
                _uiState.value = DetailUiState.Error("No se pudo borrar el producto. Revisa tu conexión.")
            }
        }
    }

    companion object {
        fun provideFactory(
            productRepository: ProductRepository,
            userRepository: UserRepository,
            productId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductDetailViewModel(productRepository, userRepository, productId) as T
            }
        }
    }
}