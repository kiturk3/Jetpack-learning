package com.kiturk3.recipevault.repository

import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FakeAndroidTestRecipeRepository @Inject constructor() : RecipeRepository {
    override fun getRecipes(): Flow<Resource<List<Recipe>>> = flow {
        emit(Resource.Loading)
        delay(500) // small delay so shimmer is catchable
        emit(Resource.Success(listOf(
            Recipe(1, "Spaghetti Carbonara", 30, "Italian"),
            Recipe(2, "Chicken Tikka Masala", 45, "Indian")
        )))
    }
    // other methods with minimal implementations
    override fun getRecipeById(id: Int): Flow<Resource<Recipe>> = flow {
        emit(Resource.Success(Recipe(id, "Test Recipe", 30, "Test")))
    }
    override fun searchRecipes(query: String): Flow<Resource<List<Recipe>>> = flow {
        emit(Resource.Success(emptyList()))
    }
    override suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {}
    override fun getFavorites(): Flow<List<Recipe>> = flowOf(emptyList())
}