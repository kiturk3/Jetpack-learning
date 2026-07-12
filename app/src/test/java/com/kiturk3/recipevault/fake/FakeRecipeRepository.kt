package com.kiturk3.recipevault.fake

import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FakeRecipeRepository : RecipeRepository {

    private val recipesFlow = MutableStateFlow<List<Recipe>>(emptyList())
    private val favoritesFlow = MutableStateFlow<List<Recipe>>(emptyList())
    var shouldReturnError = false
    var errorMessage = "Test error"

    fun setRecipes(recipes: List<Recipe>) {
        recipesFlow.value = recipes
    }

    override fun getRecipes(): Flow<Resource<List<Recipe>>> = flow {
        emit(Resource.Loading)
        if (shouldReturnError) {
            emit(Resource.Error(errorMessage))
        } else {
            emit(Resource.Success(recipesFlow.value))
        }
    }

    override fun getRecipeById(id: Int): Flow<Resource<Recipe>> = flow {
        emit(Resource.Loading)
        if (shouldReturnError) {
            emit(Resource.Error(errorMessage))
        } else {
            val recipe = recipesFlow.value.find { it.id == id }
            if (recipe != null) {
                emit(Resource.Success(recipe))
            } else {
                emit(Resource.Error("Recipe not found"))
            }
        }
    }

    override fun searchRecipes(query: String): Flow<Resource<List<Recipe>>> = flow {
        emit(Resource.Loading)
        if (shouldReturnError) {
            emit(Resource.Error(errorMessage))
        } else {
            val results = recipesFlow.value.filter {
                it.title.contains(query, ignoreCase = true)
            }
            emit(Resource.Success(results))
        }
    }

    override suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        val current = recipesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == recipeId }
        if (index != -1) {
            current[index] = current[index].copy(isFav = isFavorite)
            recipesFlow.value = current
        }
        if (isFavorite) {
            val recipe = recipesFlow.value.find { it.id == recipeId }
            recipe?.let { favoritesFlow.value = favoritesFlow.value + it }
        } else {
            favoritesFlow.value = favoritesFlow.value.filter { it.id != recipeId }
        }
    }

    override fun getFavorites(): Flow<List<Recipe>> = favoritesFlow
}