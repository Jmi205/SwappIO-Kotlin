package uniandes.isis3510.rewereable.ui.screens.donate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uniandes.isis3510.rewereable.domain.model.Charity
import uniandes.isis3510.rewereable.domain.repository.CharityRepository

sealed class DonateUiState {
    object Loading : DonateUiState()

    data class Success(
        val charities: List<Charity>,
        val filteredCharities: List<Charity>,
        val categories: List<String>,
        val selectedCategory: String = "Children",
        val searchQuery: String = "",
        val featuredCharity: Charity? = null
    ) : DonateUiState()

    data class Error(val message: String) : DonateUiState()
}

class DonateViewModel(
    private val charityRepository: CharityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DonateUiState>(DonateUiState.Loading)
    val uiState: StateFlow<DonateUiState> = _uiState.asStateFlow()

    private var allCharities: List<Charity> = emptyList()

    init {
        loadDonateData()
    }

    private fun loadDonateData() {
        viewModelScope.launch {
            _uiState.value = DonateUiState.Loading

            val categoriesResult = charityRepository.getCategories()
            val charitiesResult = charityRepository.getCharities()

            if (categoriesResult.isSuccess && charitiesResult.isSuccess) {
                allCharities = charitiesResult.getOrDefault(emptyList())
                val categories = categoriesResult.getOrDefault(emptyList())
                val defaultCategory = "Children"

                val filtered = filterCharities(
                    charities = allCharities,
                    query = "",
                    selectedCategory = defaultCategory
                )

                _uiState.value = DonateUiState.Success(
                    charities = allCharities,
                    filteredCharities = filtered,
                    categories = categories,
                    selectedCategory = defaultCategory,
                    searchQuery = "",
                    featuredCharity = allCharities.firstOrNull { it.isFeatured }
                )
            } else {
                val categoriesError = categoriesResult.exceptionOrNull()?.message
                val charitiesError = charitiesResult.exceptionOrNull()?.message

                _uiState.value = DonateUiState.Error(
                    "Error categories: $categoriesError | Error charities: $charitiesError"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        val currentState = _uiState.value
        if (currentState is DonateUiState.Success) {
            val filtered = filterCharities(
                charities = allCharities,
                query = query,
                selectedCategory = currentState.selectedCategory
            )

            _uiState.value = currentState.copy(
                searchQuery = query,
                filteredCharities = filtered
            )
        }
    }

    fun onCategorySelected(category: String) {
        val currentState = _uiState.value
        if (currentState is DonateUiState.Success) {
            val filtered = filterCharities(
                charities = allCharities,
                query = currentState.searchQuery,
                selectedCategory = category
            )

            _uiState.value = currentState.copy(
                selectedCategory = category,
                filteredCharities = filtered
            )
        }
    }

    private fun filterCharities(
        charities: List<Charity>,
        query: String,
        selectedCategory: String
    ): List<Charity> {
        return charities.filter { charity ->
            val matchesSearch =
                query.isBlank() ||
                        charity.name.contains(query, ignoreCase = true) ||
                        charity.location.contains(query, ignoreCase = true) ||
                        charity.description.contains(query, ignoreCase = true)

            val matchesCategory =
                selectedCategory == "Near Me" ||
                        charity.tags.any { it.equals(selectedCategory, ignoreCase = true) }

            matchesSearch && matchesCategory
        }
    }
}