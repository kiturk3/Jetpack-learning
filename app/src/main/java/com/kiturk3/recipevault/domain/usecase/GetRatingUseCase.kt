package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.model.Rating
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRatingUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(recipeId: String): Flow<Rating?> =
        repository.getRating(recipeId)
}