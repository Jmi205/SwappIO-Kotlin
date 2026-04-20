package uniandes.isis3510.rewereable.ui.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.model.Checkout
import uniandes.isis3510.rewereable.domain.repository.ProductRepository
import uniandes.isis3510.rewereable.domain.repository.CheckoutRepository
import uniandes.isis3510.rewereable.domain.repository.UserRepository

sealed class CheckoutUiState {
    object Idle : CheckoutUiState()
    object Loading : CheckoutUiState()
    object Success : CheckoutUiState()
    data class Error(val message: String) : CheckoutUiState()
}

class CheckoutViewModel(
    private val productId: String,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val checkoutRepository: CheckoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Loading) // Iniciamos en Loading para traer los datos
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    // Datos del formulario
    val cardNumber = MutableStateFlow("")
    val cardHolder = MutableStateFlow("")
    val document = MutableStateFlow("")
    val expiryDate = MutableStateFlow("")
    val cvv = MutableStateFlow("")

    // Datos Reales que traeremos de Firebase
    val productName = MutableStateFlow("")
    val productPrice = MutableStateFlow(0.0)
    val productImage = MutableStateFlow("")
    val sellerId = MutableStateFlow("")
    val sellerName = MutableStateFlow("")

    init {
        loadCheckoutData()
    }

    private fun loadCheckoutData() {
        viewModelScope.launch {
            try {
                val productResult = productRepository.getProductById(productId)

                val product = productResult.getOrNull()

                if (product != null) {
                    productName.value = product.name
                    productPrice.value = product.price
                    productImage.value = if (product.images.isNotEmpty()) product.images[0] else ""
                    sellerId.value = product.ownerId

                    val sellerResult = userRepository.getUserProfile(product.ownerId)
                    val seller = sellerResult.getOrNull()

                    if (seller != null) {
                        sellerName.value = "${seller.name} ${seller.lastname}"
                    } else {
                        sellerName.value = "Usuario Desconocido"
                    }

                    _uiState.value = CheckoutUiState.Idle
                } else {
                    _uiState.value = CheckoutUiState.Error("Product not found")
                }
            } catch (e: Exception) {
                _uiState.value = CheckoutUiState.Error(e.message ?: "Error loading data")
            }
        }
    }

    fun processPayment() {
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Loading

            delay(2500)

            val last4Digits = if (cardNumber.value.length >= 4) cardNumber.value.takeLast(4) else "0000"

            val purchase = Checkout(
                productId = productId,
                sellerId = sellerId.value,
                price = productPrice.value,
                paymentMethodLast4 = last4Digits
            )

            val result = checkoutRepository.createPurchase(purchase)

            result.fold(
                onSuccess = {
                    _uiState.value = CheckoutUiState.Success
                },
                onFailure = { error ->
                    _uiState.value = CheckoutUiState.Error(error.message ?: "Payment failed")
                }
            )
        }
    }

    companion object {
        fun provideFactory(
            productId: String,
            productRepository: ProductRepository,
            userRepository: UserRepository,
            checkoutRepository: CheckoutRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CheckoutViewModel(productId, productRepository, userRepository, checkoutRepository) as T
                }
            }
    }
}