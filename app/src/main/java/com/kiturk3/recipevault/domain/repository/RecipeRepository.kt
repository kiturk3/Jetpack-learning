package com.kiturk3.recipevault.domain.repository

import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.Rating
import com.kiturk3.recipevault.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getRecipes(): Flow<Resource<List<Recipe>>>
    fun getRecipeById(id: Int): Flow<Resource<Recipe>>
    fun searchRecipes(query: String): Flow<Resource<List<Recipe>>>
    suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean)
    fun getFavorites(): Flow<List<Recipe>>

    fun getUserRecipes(): Flow<List<Recipe>>
    suspend fun addUserRecipe(recipe: Recipe): Long
    suspend fun updateUserRecipe(recipe: Recipe)
    suspend fun deleteUserRecipe(id: Int)

    suspend fun upsertRating(recipeId: String, rating: Int, notes: String)
    fun getRating(recipeId: String): Flow<Rating?>
}