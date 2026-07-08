package com.kiturk3.recipevault.data.repository

import com.kiturk3.recipevault.data.remote.MealApiService
import com.kiturk3.recipevault.data.remote.mapper.toRecipe
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class RecipeRepositoryImpl @Inject constructor(
    private val apiService: MealApiService
) : RecipeRepository {

    override fun getRecipes(): Flow<List<Recipe>> = flow {
        val response = apiService.searchMeals(query = "")
        val recipes = response.meals?.map { it.toRecipe() } ?: emptyList()
        emit(recipes)
    }

    override fun getRecipeById(id: Int): Flow<Recipe?> = flow {
        val response = apiService.getMealById(id.toString())
        val recipe = response.meals?.firstOrNull()?.toRecipe()
        emit(recipe)
    }

    override fun searchRecipes(query: String): Flow<List<Recipe>> = flow {
        val response = apiService.searchMeals(query = query)
        val recipes = response.meals?.map { it.toRecipe() } ?: emptyList()
        emit(recipes)
    }

    override suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        // TODO: Implement local database update for favorites
    }
}