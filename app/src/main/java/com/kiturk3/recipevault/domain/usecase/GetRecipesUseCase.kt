package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecipesUseCase @Inject constructor(private val repository: RecipeRepository) {
    operator fun invoke(): Flow<Resource<List<Recipe>>> = repository.getRecipes()
    operator fun invoke(id: Int): Flow<Resource<Recipe>> = repository.getRecipeById(id)
}