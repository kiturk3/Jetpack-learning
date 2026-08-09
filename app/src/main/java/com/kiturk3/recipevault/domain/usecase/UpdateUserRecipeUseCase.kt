package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import javax.inject.Inject

class UpdateUserRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: Recipe) =
        repository.updateUserRecipe(recipe)
}