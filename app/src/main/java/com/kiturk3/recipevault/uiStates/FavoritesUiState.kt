package com.kiturk3.recipevault.uiStates

import com.kiturk3.recipevault.domain.model.Recipe

sealed class FavoritesUiState {
    data object Loading : FavoritesUiState()
    data object Empty : FavoritesUiState()
    data class Success(val favorites: List<Recipe>) : FavoritesUiState()
}