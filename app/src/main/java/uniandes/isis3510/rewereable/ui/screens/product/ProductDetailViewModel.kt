package uniandes.isis3510.rewereable.ui.screens.product

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
    data class Success(val product: Product, val owner: User) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository, // ¡NUEVO!
    private val productId: String
) : ViewModel() {

    companion object {
        fun provideFactory(
            productRepository: ProductRepository,
            userRepository: UserRepository, // ¡NUEVO!
            productId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductDetailViewModel(productRepository, userRepository, productId) as T
            }
        }
    }

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            val productResult = productRepository.getProductById(productId)

            Log.d("ProductDetailViewModel", "Product ID: $productId")

            if (productResult.isSuccess) {
                val product = productResult.getOrNull()!!

                val ownerResult = userRepository.getUserProfile(product.ownerId)

                if (ownerResult.isSuccess) {
                    val owner = ownerResult.getOrNull()!!
                    _uiState.value = DetailUiState.Success(product, owner)
                } else {
                    val unknownOwner = User(
                        id = product.ownerId, name = "Usuario", lastname = "Desconocido",
                        email = "", balance = 0.0, location = "", number = "",
                        memberSince = 0L, profilePictureUrl = null
                    )
                    _uiState.value = DetailUiState.Success(product, unknownOwner)
                }
            } else {
                _uiState.value = DetailUiState.Error("Error al cargar el producto")
            }
        }
    }
}