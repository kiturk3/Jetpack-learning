package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecipesUseCase(private val repository: RecipeRepository) {
    operator fun invoke(): Flow<List<Recipe>> = repository.getRecipes()
}