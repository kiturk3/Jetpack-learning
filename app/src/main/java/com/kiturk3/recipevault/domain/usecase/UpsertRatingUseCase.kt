package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.repository.RecipeRepository
import javax.inject.Inject

class UpsertRatingUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipeId: String, rating: Int, notes: String) =
        repository.upsertRating(recipeId, rating, notes)
}