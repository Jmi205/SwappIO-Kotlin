package uniandes.isis3510.rewereable.ui.screens.home

import uniandes.isis3510.rewereable.domain.model.Product
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.repository.ProductRepository
import uniandes.isis3510.rewereable.domain.repository.UserRepository

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val products: List<Product>,
        val categories: List<String>,
        val searchQuery: String = "",
        val favoriteIds: Set<String> = emptySet()
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allProducts: List<Product> = emptyList()

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
                allProducts = productsResult.getOrDefault(emptyList())
                _uiState.value = HomeUiState.Success(
                    categories = categoriesResult.getOrDefault(emptyList()),
                    products = allProducts
                )
            } else {
                _uiState.value = HomeUiState.Error("No se pudieron cargar los datos.")
            }
        }
    }
    fun onSearchQueryChanged(query: String){
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val filteredProducts = if (query.isBlank()) {
                allProducts
            } else {
                allProducts.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.brand?.contains(query, ignoreCase = true) == true
                }
            }
            _uiState.value = currentState.copy(
                searchQuery = query,
                products = filteredProducts
            )
        }
    }

    fun toggleFavorite(productId: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val currentFavorites = currentState.favoriteIds.toMutableSet()
            val isCurrentlyFavorite = currentFavorites.contains(productId)

            if (isCurrentlyFavorite) {
                currentFavorites.remove(productId)
                // Aquí llamarías: userRepository.removeFromFavorites("usuario_actual", productId)
            } else {
                currentFavorites.add(productId)
                // Aquí llamarías: userRepository.addToFavorites("usuario_actual", productId)
            }

            _uiState.value = currentState.copy(favoriteIds = currentFavorites)
        }
    }
}