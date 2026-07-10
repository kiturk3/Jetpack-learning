package com.kiturk3.recipevault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kiturk3.recipevault.data.local.dao.FavoriteDao
import com.kiturk3.recipevault.data.local.dao.RecipeDao
import com.kiturk3.recipevault.data.local.entity.FavoriteEntity
import com.kiturk3.recipevault.data.local.entity.RecipeEntity

@Database(
    entities = [FavoriteEntity::class, RecipeEntity::class],
    version = 2,
    exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recipeDao(): RecipeDao
}