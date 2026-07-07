package com.kiturk3.recipevault.data.repository

import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class RecipeRepositoryImpl @Inject constructor() : RecipeRepository {
    private var recipes = listOf(
        Recipe(1, "Spaghetti Carbonara", 30, "Italian", false),
        Recipe(2, "Chicken Tikka Masala", 45, "Indian", false),
        Recipe(3, "Pad Thai", 25, "Thai", false)
    )

    override fun getRecipes(): Flow<List<Recipe>> = flow {
        delay(1500.milliseconds)
        emit(recipes)
    }

    override fun getRecipeById(id: Int): Flow<Recipe?> {
        TODO("Not yet implemented")
    }

    override fun searchRecipes(query: String): Flow<List<Recipe>> {
        TODO("Not yet implemented")
    }

    override suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        recipes = recipes.map { recipe ->
            if (recipe.id == recipeId) recipe.copy(isFav = isFavorite) else recipe
        }
    }
}