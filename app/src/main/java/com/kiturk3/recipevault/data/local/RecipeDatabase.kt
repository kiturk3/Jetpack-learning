package com.kiturk3.recipevault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kiturk3.recipevault.data.local.dao.FavoriteDao
import com.kiturk3.recipevault.data.local.entity.FavoriteEntity

@Database(
    entities = [FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}