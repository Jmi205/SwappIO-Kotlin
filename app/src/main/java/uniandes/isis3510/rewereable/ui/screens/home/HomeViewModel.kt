package uniandes.isis3510.rewereable.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
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

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val currentUser: User,
        val products: List<Product>,
        val filteredProducts: List<Product>,
        val tag: List<String>,
        val selectedTag: String,
        val searchQuery: String = "",
        val favoriteIds: Set<String> = emptySet()
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var allProducts: List<Product> = emptyList()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val currentUserId = auth.currentUser?.uid
            Log.d("User ID", "User ID: $currentUserId" )

            if (currentUserId != null) {
                val userResult = userRepository.getUserProfile(currentUserId)
                val tagsResult = productRepository.getTags()
                val productsResult = productRepository.getTrendingProducts()

                if (tagsResult.isSuccess && productsResult.isSuccess && userResult.isSuccess) {
                    allProducts = productsResult.getOrDefault(emptyList())
                    val tags = tagsResult.getOrDefault(emptyList())
                    val currentUser = userResult.getOrNull()!!

                    val initialFavorites = currentUser.favorites.toSet()

                    val defaultTag = tags.firstOrNull() ?: "Trending"

                    val filtered = filteredProducts(
                        products = allProducts,
                        query = "",
                        selectedTag = defaultTag
                    )

                    _uiState.value = HomeUiState.Success(
                        currentUser = currentUser,
                        products = allProducts,
                        filteredProducts = filtered,
                        tag = tags,
                        selectedTag = defaultTag,
                        searchQuery = "",
                        favoriteIds = initialFavorites
                    )
                } else {
                    _uiState.value = HomeUiState.Error("No se pudieron cargar los datos.")
                }

            }else {
                _uiState.value = HomeUiState.Error("Usuario no autenticado | currentUserId")
            }



        }
    }

    fun onSearchQueryChanged(query: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val filtered = filteredProducts(
                products = allProducts,
                query = query,
                selectedTag = currentState.selectedTag
            )
            _uiState.value = currentState.copy(
                searchQuery = query,
                filteredProducts = filtered // Actualizamos la lista filtrada
            )
        }
    }

    fun onTagSelected(tag: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val filtered = filteredProducts(
                products = allProducts,
                query = currentState.searchQuery,
                selectedTag = tag
            )

            _uiState.value = currentState.copy(
                selectedTag = tag,
                filteredProducts = filtered
            )
        }
    }

    private fun filteredProducts(
        products: List<Product>,
        query: String,
        selectedTag: String
    ): List<Product> {
        return products.filter { product ->
            val matchesSearch = query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    (product.brand?.contains(query, ignoreCase = true) == true) ||
                    product.description.contains(query, ignoreCase = true)

            // Si el tag es "Trending" o "All", mostramos todos. Si no, buscamos coincidencias.
            val matchesCategory = selectedTag == "Trending" || selectedTag == "All" ||
                    product.styleTags.any { it.equals(selectedTag, ignoreCase = true) }

            matchesSearch && matchesCategory
        }
    }

    fun toggleFavorite(productId: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            val currentFavs = currentState.favoriteIds.toMutableSet()
            val userId = currentState.currentUser.id

            viewModelScope.launch {
                if (currentFavs.contains(productId)) {
                    currentFavs.remove(productId)
                    userRepository.removeFromFavorites(userId, productId)
                } else {
                    currentFavs.add(productId)
                    userRepository.addToFavorites(userId, productId)
                }
                _uiState.value = currentState.copy(favoriteIds = currentFavs)
            }
        }
    }
}