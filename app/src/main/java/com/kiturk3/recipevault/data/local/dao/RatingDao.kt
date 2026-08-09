package com.kiturk3.recipevault.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kiturk3.recipevault.data.local.entity.RatingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRating(rating: RatingEntity)

    @Query("SELECT * FROM ratings WHERE recipeId = :recipeId")
    fun getRating(recipeId: String): Flow<RatingEntity?>

    @Query("DELETE FROM ratings WHERE recipeId = :recipeId")
    suspend fun deleteRating(recipeId: String)
}