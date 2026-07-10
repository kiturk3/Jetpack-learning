package com.kiturk3.recipevault.domain.usecase

import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
){
    operator fun invoke(query: String): Flow<Resource<List<Recipe>>> {
        return repository.searchRecipes(query)
    }
}