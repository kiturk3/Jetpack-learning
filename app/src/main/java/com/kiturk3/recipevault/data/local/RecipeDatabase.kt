package com.kiturk3.recipevault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kiturk3.recipevault.data.local.dao.FavoriteDao
import com.kiturk3.recipevault.data.local.dao.RatingDao
import com.kiturk3.recipevault.data.local.dao.RecipeDao
import com.kiturk3.recipevault.data.local.dao.UserRecipeDao
import com.kiturk3.recipevault.data.local.entity.FavoriteEntity
import com.kiturk3.recipevault.data.local.entity.RatingEntity
import com.kiturk3.recipevault.data.local.entity.RecipeEntity
import com.kiturk3.recipevault.data.local.entity.UserRecipeEntity

@Database(
    entities = [
        FavoriteEntity::class,
        RecipeEntity::class,
        UserRecipeEntity::class,
        RatingEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recipeDao(): RecipeDao
    abstract fun userRecipeDao(): UserRecipeDao  // ← new
    abstract fun ratingDao(): RatingDao
}