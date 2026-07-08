package com.kiturk3.recipevault.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kiturk3.recipevault.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT mealId FROM favorites")
    fun getFavoriteIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE mealId = :mealId")
    suspend fun removeFavorite(mealId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE mealId = :mealId)")
    suspend fun isFavorite(mealId: String): Boolean
}