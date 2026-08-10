package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.repository.RecipeRepository
import javax.inject.Inject

class RefreshRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke() = repository.refreshRecipesFromNetwork()
}