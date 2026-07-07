package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.repository.RecipeRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(private val repository: RecipeRepository) {
    suspend operator fun invoke(recipeId: Int, isFavorite: Boolean) =
        repository.toggleFavorite(recipeId, isFavorite)
}