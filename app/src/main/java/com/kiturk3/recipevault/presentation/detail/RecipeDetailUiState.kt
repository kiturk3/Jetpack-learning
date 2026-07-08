package com.kiturk3.recipevault.presentation.detail

import com.kiturk3.recipevault.domain.model.Recipe

sealed class RecipeDetailUiState {
    data object Loading : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
    data class Success(val recipe: Recipe) : RecipeDetailUiState()
}