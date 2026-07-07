package com.kiturk3.recipevault.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.usecase.GetRecipesUseCase
import com.kiturk3.recipevault.domain.usecase.ToggleFavoriteUseCase

import com.kiturk3.recipevault.model.RecipeItem
import com.kiturk3.recipevault.uiStates.RecipeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val getToggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel(){
    private val _uiState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadRecipes()
        observeSearch()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            try {
                getRecipesUseCase().collect {
                    recipes -> updateSuccess(recipes, _searchQuery.value)
                }
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
        val recipe = current.recipes.find { it.id == id } ?: return
        viewModelScope.launch {
            getToggleFavoriteUseCase(id, !recipe.isFav)
            getRecipesUseCase().collect {
                recipes -> updateSuccess(recipes, _searchQuery.value)
            }
        }
    }

    private fun updateSuccess(recipes: List<Recipe>, query: String) {
        _uiState.value = RecipeUiState.Success(
            recipes = recipes,
            filteredRecipes = recipes.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.cuisine.contains(query, ignoreCase = true)
            },
            searchQuery = query
        )
    }
}