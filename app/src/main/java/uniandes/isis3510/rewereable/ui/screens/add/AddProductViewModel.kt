package uniandes.isis3510.rewereable.ui.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.model.Product
import uniandes.isis3510.rewereable.domain.model.ProductStatus
import uniandes.isis3510.rewereable.domain.repository.ProductRepository
import uniandes.isis3510.rewereable.domain.repository.UserRepository

sealed class AddProductUiState {
    object Idle : AddProductUiState()
    object Loading : AddProductUiState()
    object Success : AddProductUiState()
    data class Error(val message: String) : AddProductUiState()
}

class AddProductViewModel(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<AddProductUiState>(AddProductUiState.Idle)
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()

    var title = MutableStateFlow("")
    var brand = MutableStateFlow("")
    var price = MutableStateFlow("")
    var size = MutableStateFlow("M")
    var condition = MutableStateFlow("Good")
    var description = MutableStateFlow("")
    var location = MutableStateFlow("")

    val selectedLatLng = MutableStateFlow<LatLng?>(null)

    var selectedImages = MutableStateFlow<List<String>>(emptyList())

    fun submitProduct() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            _uiState.value = AddProductUiState.Error("Debes iniciar sesión para publicar.")
            return
        }

        // Validación básica
        if (title.value.isBlank() || price.value.isBlank() || location.value.isBlank()) {
            _uiState.value = AddProductUiState.Error("Por favor, llena el título, el precio y la ubicación.")
            return
        }

        if (selectedLatLng.value == null) {
            _uiState.value = AddProductUiState.Error("Selecciona una ubicación en el mapa.")
            return
        }

        val priceDouble = price.value.toDoubleOrNull() ?: 0.0

        _uiState.value = AddProductUiState.Loading

        viewModelScope.launch {

            val uploadedUrls = emptyList<String>()

            val newProduct = Product(
                name = title.value,
                brand = brand.value.ifBlank { null },
                price = priceDouble,
                size = size.value,
                condition = condition.value,
                description = description.value,
                location = location.value,
                latitude = selectedLatLng.value?.latitude,
                longitude = selectedLatLng.value?.longitude,
                images = uploadedUrls, // Se reemplazará con las de Cloudinary luego
                ownerId = currentUserId,
                status = ProductStatus.AVAILABLE
            )

            val result = productRepository.createProductListing(newProduct)

            if (result.isSuccess) {
                val updateListingResult = userRepository.addToListings(currentUserId, newProduct.id)

                if (updateListingResult.isSuccess) {
                    _uiState.value = AddProductUiState.Success
                } else {
                    _uiState.value = AddProductUiState.Error("Producto creado, pero falló al vincularlo a tu perfil.")
                }
            } else {
                _uiState.value = AddProductUiState.Error("Error al publicar el producto.")
            }
        }
    }

    companion object {
        fun provideFactory(
            productRepository: ProductRepository,
            userRepository: UserRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddProductViewModel(productRepository, userRepository) as T
                }
            }
    }
}