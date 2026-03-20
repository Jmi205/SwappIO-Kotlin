package uniandes.isis3510.rewereable.ui.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import uniandes.isis3510.rewereable.domain.model.Product
import uniandes.isis3510.rewereable.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.model.ProductStatus

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val product: Product) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
    private val productId: String
) : ViewModel() {

    companion object {
        fun provideFactory(
            repository: ProductRepository,
            productId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductDetailViewModel(repository, productId) as T
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
            val result = productRepository.getProductById(productId)
            result.fold(
                onSuccess = { product ->
                    // Si el backend aún no retorna nada, simulamos los datos de tu diseño
                    val finalProduct = product ?: Product(
                        id = productId,
                        name = "Vintage 90s Denim Jacket",
                        description = "Authentic vintage denim jacket from the 90s. Excellent condition with natural fading. Perfect for oversized looks. Recently dry cleaned. No stains or tears.",
                        price = 120000.0,
                        size = "M",
                        brand = "Original Brand",
                        location = "Chapinero, Bogotá",
                        images = emptyList(),
                        stateTags = listOf("Used - Like New", "Unisex"),
                        styleTags = emptyList(),
                        status = ProductStatus.AVAILABLE,
                        ownerId = "Maria G."
                    )
                    _uiState.value = DetailUiState.Success(finalProduct)
                },
                onFailure = { _uiState.value = DetailUiState.Error("Error loading product") }
            )
        }
    }
}