package uniandes.isis3510.rewereable.ui.screens.home

import uniandes.isis3510.rewereable.domain.model.Product
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.repository.ProductRepository

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val products: List<Product>, val categories: List<String>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            // Hacemos ambas peticiones
            val categoriesResult = productRepository.getCategories()
            val productsResult = productRepository.getTrendingProducts()

            if (categoriesResult.isSuccess && productsResult.isSuccess) {
                _uiState.value = HomeUiState.Success(
                    categories = categoriesResult.getOrDefault(emptyList()),
                    products = productsResult.getOrDefault(emptyList())
                )
            } else {
                _uiState.value = HomeUiState.Error("No se pudieron cargar los datos.")
            }
        }
    }
}