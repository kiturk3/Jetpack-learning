package com.kiturk3.recipevault.data.repository

import com.kiturk3.recipevault.data.local.dao.FavoriteDao
import com.kiturk3.recipevault.data.local.dao.RatingDao
import com.kiturk3.recipevault.data.local.dao.RecipeDao
import com.kiturk3.recipevault.data.local.dao.UserRecipeDao
import com.kiturk3.recipevault.data.local.entity.FavoriteEntity
import com.kiturk3.recipevault.data.local.entity.RatingEntity
import com.kiturk3.recipevault.data.remote.MealApiService
import com.kiturk3.recipevault.data.remote.mapper.toEntity
import com.kiturk3.recipevault.data.remote.mapper.toRecipe
import com.kiturk3.recipevault.data.remote.mapper.toUserRecipeEntity
import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.Rating
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
    private val recipeDao: RecipeDao,
    private val ratingDao: RatingDao,
    private val userRecipeDao: UserRecipeDao
) : RecipeRepository {

    override fun getRecipes(): Flow<Resource<List<Recipe>>> = flow {
        emit(Resource.Loading)
        val favoriteIds = favoriteDao.getFavoriteIds().first()
        //Try to load from database first
        val cached = recipeDao.getAllRecipes().first()
        val userRecipes = userRecipeDao.getAllUserRecipes().first()
            .map { it.toRecipe() }


        if (cached.isNotEmpty() || userRecipes.isNotEmpty()){
            val cachedRecipes = cached.map {
                it.toRecipe(isFav = it.mealId in favoriteIds)
            }
            emit(Resource.Success(userRecipes + cachedRecipes))
        }

        try{
            val response = apiService.searchMeals(query = "")
            val entities = response.meals?.map { it.toEntity() } ?: emptyList()

            recipeDao.clearRecipes()
            recipeDao.insertRecipes(entities)

            val updatedFavoriteIds = favoriteDao.getFavoriteIds().first()
            val updatedUserRecipes = userRecipeDao.getAllUserRecipes().first()
                .map { it.toRecipe() }
            val apiRecipes = entities.map {
                it.toRecipe(isFav = it.mealId in updatedFavoriteIds)
            }
            emit(Resource.Success(updatedUserRecipes + apiRecipes))
        }
        catch (e: Exception){
            emit(Resource.Error(e.message ?: "Failed to fetch recipes",
                data = if (cached.isNotEmpty() || userRecipes.isNotEmpty()){
                    cached.map { it.toRecipe(isFav = it.mealId in favoriteIds) }
                }
                else null
            ))
        }
    }

    override fun getRecipeById(id: Int): Flow<Resource<Recipe>> = flow {
        emit(Resource.Loading)

        // Check user recipes first
        val userRecipe = userRecipeDao.getUserRecipeById(id)
        if (userRecipe != null) {
            emit(Resource.Success(userRecipe.toRecipe()))
            return@flow  // user recipe found — no API call needed
        }

        // Fall back to API cache + network for TheMealDB recipes
        val cached = recipeDao.getRecipeById(id.toString()).first()
        val isFavCached = favoriteDao.isFavorite(id.toString())
        if (cached != null) emit(Resource.Success(cached.toRecipe(isFav = isFavCached)))

        try {
            val response = apiService.getMealById(id.toString())
            val dto = response.meals?.firstOrNull()
            if (dto != null) {
                recipeDao.insertRecipes(listOf(dto.toEntity()))
                val isFav = favoriteDao.isFavorite(id.toString())
                emit(Resource.Success(dto.toRecipe().copy(isFav = isFav)))
            } else {
                if (cached == null) emit(Resource.Error("Recipe not found"))
            }
        } catch (e: Exception) {
            if (cached == null) emit(Resource.Error(e.message ?: "Failed to load recipe"))
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
                    thumbnailUrl = entity.thumbnailUrl,
                    instructions = null
                )
            }
        }
    }


    override fun getUserRecipes(): Flow<List<Recipe>> {
        return userRecipeDao.getAllUserRecipes().map { entities ->
            entities.map { it.toRecipe() }
        }
    }

    override suspend fun addUserRecipe(recipe: Recipe): Long {
        return userRecipeDao.insertUserRecipe(recipe.toUserRecipeEntity())
    }

    override suspend fun updateUserRecipe(recipe: Recipe) {
        userRecipeDao.updateUserRecipe(recipe.toUserRecipeEntity())
    }

    override suspend fun deleteUserRecipe(id: Int) {
        userRecipeDao.deleteUserRecipe(id)
        // Also delete associated rating
        ratingDao.deleteRating("user_$id")
    }

    // Ratings
    override suspend fun upsertRating(recipeId: String, rating: Int, notes: String) {
        ratingDao.upsertRating(RatingEntity(recipeId = recipeId, rating = rating, notes = notes))
    }

    override fun getRating(recipeId: String): Flow<Rating?> {
        return ratingDao.getRating(recipeId).map { entity ->
            entity?.let { Rating(it.recipeId, it.rating, it.notes) }
        }
    }


}