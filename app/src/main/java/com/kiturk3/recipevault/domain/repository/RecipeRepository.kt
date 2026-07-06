package com.kiturk3.recipevault.domain.repository

import com.kiturk3.recipevault.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getRecipes(): Flow<List<Recipe>>
    suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean)
}