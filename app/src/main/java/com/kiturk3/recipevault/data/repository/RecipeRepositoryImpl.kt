package com.kiturk3.recipevault.data.repository

import com.kiturk3.recipevault.data.local.dao.FavoriteDao
import com.kiturk3.recipevault.data.local.dao.RecipeDao
import com.kiturk3.recipevault.data.local.entity.FavoriteEntity
import com.kiturk3.recipevault.data.remote.MealApiService
import com.kiturk3.recipevault.data.remote.mapper.toEntity
import com.kiturk3.recipevault.data.remote.mapper.toRecipe
import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.emptyList

class RecipeRepositoryImpl @Inject constructor(
    private val apiService: MealApiService,
    private val favoriteDao: FavoriteDao,
    private val recipeDao: RecipeDao
) : RecipeRepository {

    override fun getRecipes(): Flow<Resource<List<Recipe>>> = flow {
        emit(Resource.Loading)
        val favoriteIds = favoriteDao.getFavoriteIds().first()
        //Try to load from database first
        val cached = recipeDao.getAllRecipes().first()
        if (cached.isNotEmpty()){
            emit(Resource.Success(cached.map { it.toRecipe(isFav = it.mealId in favoriteIds) }))
        }

        try{
            val response = apiService.searchMeals(query = "")
            val entities = response.meals?.map { it.toEntity() } ?: emptyList()

            recipeDao.clearRecipes()
            recipeDao.insertRecipes(entities)

            val updatedFavoriteIds = favoriteDao.getFavoriteIds().first()
            val updatedRecipes = entities.map {
                it.toRecipe(isFav = it.mealId in updatedFavoriteIds)
            }
            emit(Resource.Success(updatedRecipes))
        }
        catch (e: Exception){
            emit(Resource.Error(e.message ?: "Failed to fetch recipes",
                data = if (cached.isNotEmpty()){
                    cached.map { it.toRecipe(isFav = it.mealId in favoriteIds) }
                }
                else null
            ))
        }
    }

    override fun getRecipeById(id: Int): Flow<Resource<Recipe>> = flow {
        emit(Resource.Loading)

        val cached = recipeDao.getRecipeById(id.toString()).first()
        val isFavCached = favoriteDao.isFavorite(id.toString())

        if (cached != null){
            emit(Resource.Success(cached.toRecipe(isFav = isFavCached)))
        }

        try{
            val response = apiService.getMealById(id.toString())
            val dto = response.meals?.firstOrNull()
            if (dto != null){
                recipeDao.insertRecipes(listOf(dto.toEntity()))
                val isFav = favoriteDao.isFavorite(id.toString())
                emit(Resource.Success(dto.toRecipe().copy(isFav = isFav)))
            }
            else{
                emit(Resource.Error("Recipe not found"))
            }
        }catch (e: Exception){
            emit(Resource.Error(
                message = e.message ?: "Failed to load recipe",
                data = cached?.toRecipe(isFav = isFavCached)
            ))
        }
    }

    override fun searchRecipes(query: String): Flow<Resource<List<Recipe>>> = flow {
        emit(Resource.Loading)
        val favoriteIds = favoriteDao.getFavoriteIds().first()

        try {
            val response = apiService.searchMeals(query = query)
            val recipes = response.meals?.map { it.toRecipe().copy(isFav = it.id in favoriteIds) } ?: emptyList()
            emit(Resource.Success(recipes))
        }catch (e: Exception){
            val cached = recipeDao.searchRecipes(query).first()
            emit(Resource.Error(
                message = "Network unavailable — showing cached results",
                data = cached.map { it.toRecipe(isFav = it.mealId in favoriteIds) }
            ))
        }
    }

    override suspend fun toggleFavorite(recipeId: Int, isFavorite: Boolean) {
        val mealID = recipeId.toString()
        if(isFavorite){
            val cached = recipeDao.getRecipeById(mealID).first()
            favoriteDao.addFavorite(
                FavoriteEntity(
                    mealId = mealID,
                    title = cached?.title ?: "",
                    cuisine = cached?.cuisine ?: "",
                    thumbnailUrl = cached?.thumbnailUrl
                )
            )
        }
        else{
            favoriteDao.removeFavorite(mealID)
        }
    }

    override fun getFavorites(): Flow<List<Recipe>> {
        return favoriteDao.getAllFavorites().map { entities ->
            entities.map {
                entity ->
                Recipe(
                    id = entity.mealId.toIntOrNull() ?: 0,
                    title = entity.title,
                    duration = 0,
                    cuisine = entity.cuisine,
                    isFav = true,
                    instructions = null
                )
            }
        }
    }


}