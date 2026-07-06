package com.kiturk3.recipevault.uiStates

import com.kiturk3.recipevault.model.RecipeItem

sealed class RecipeUiState {
    data object Loading : RecipeUiState()
    data class Error(val message: String) : RecipeUiState()
    data class Success(
        val recipes: List<RecipeItem> = emptyList(),
        val filteredRecipes: List<RecipeItem> = emptyList(),
        val searchQuery: String = ""
    ) : RecipeUiState()
}
