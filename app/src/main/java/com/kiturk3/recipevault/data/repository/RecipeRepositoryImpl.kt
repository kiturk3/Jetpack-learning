package com.kiturk3.recipevault.data.repository

import com.kiturk3.recipevault.data.local.dao.FavoriteDao
import com.kiturk3.recipevault.data.local.entity.FavoriteEntity
import com.kiturk3.recipevault.data.remote.MealApiService
import com.kiturk3.recipevault.data.remote.mapper.toRecipe
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val apiService: MealApiService,
    private val favoriteDao: FavoriteDao
) : RecipeRepository {

    override fun getRecipes(): Flow<List<Recipe>> = flow {
        val response = apiService.searchMeals(query = "")
        val favoriteIds = favoriteDao.getFavoriteIds().first()
        val recipes = response.meals?.map {
            it.toRecipe().copy(isFav = it.id in favoriteIds)
        } ?: emptyList()
        emit(recipes)
    }

    override fun getRecipeById(id: Int): Flow<Recipe?> = flow {
        val response = apiService.getMealById(id.toString())
        val isFav = favoriteDao.isFavorite(id.toString())
        val recipe = response.meals?.firstOrNull()?.toRecipe()?.copy(
            isFav = isFav
        )
        emit(recipe)
    }

    override fun searchRecipes(query: String): Flow<List<Recipe>> = flow {
        val response = apiService.searchMeals(query = query)
        val favoriteIds = favoriteDao.getFavoriteIds().first()
        val recipes = response.meals?.map {
            it.toRecipe().copy(isFav = it.id in favoriteIds)
        } ?: emptyList()
        emit(recipes)
    }

    override suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        val mealID = recipeId.toString()
        if(isFavorite){
            favoriteDao.addFavorite(
                FavoriteEntity(
                    mealId = mealID,
                    title = "",
                    cuisine = "",
                    thumbnailUrl = null
                )
            )
        }
        else{
            favoriteDao.removeFavorite(mealID)
        }
    }
}