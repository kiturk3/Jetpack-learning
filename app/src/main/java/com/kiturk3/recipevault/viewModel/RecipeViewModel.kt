package com.kiturk3.recipevault.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.kiturk3.recipevault.model.RecipeItem
import com.kiturk3.recipevault.uiStates.RecipeUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds

class RecipeViewModel : ViewModel(){
    private val _uiState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allRecipes = listOf(
        RecipeItem(1, "Spaghetti Carbonara", "30 min · Italian", false),
        RecipeItem(2, "Chicken Tikka Masala", "45 min · Indian", false),
        RecipeItem(3, "Pad Thai", "25 min · Thai", false)
    )

    init {
        loadRecipes()
        observeSearch()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            try {
                delay(1500.milliseconds)
                updateSuccess(allRecipes, _searchQuery.value)
            } catch (e: IOException) {
                _uiState.value = RecipeUiState.Error("Failed to load: ${e.message}")
            }
        }
    }

    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300.milliseconds)
                .collect { query ->
                    val current = _uiState.value
                    if (current is RecipeUiState.Success) {
                        updateSuccess(current.recipes, query)
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(id: Int) {
        val current = _uiState.value as? RecipeUiState.Success ?: return
        allRecipes = allRecipes.map { item ->
            if (item.id == id) item.copy(isFav = !item.isFav) else item
        }
        updateSuccess(allRecipes, current.searchQuery)
    }

    private fun updateSuccess(recipes: List<RecipeItem>, query: String) {
        _uiState.value = RecipeUiState.Success(
            recipes = recipes,
            filteredRecipes = recipes.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.durationAndCuisine.contains(query, ignoreCase = true)
            },
            searchQuery = query
        )
    }
}