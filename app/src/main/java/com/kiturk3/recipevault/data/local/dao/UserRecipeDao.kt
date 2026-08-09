package com.kiturk3.recipevault.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kiturk3.recipevault.data.local.entity.UserRecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRecipeDao {

    @Query("SELECT * FROM user_recipes ORDER BY createdAt DESC")
    fun getAllUserRecipes(): Flow<List<UserRecipeEntity>>

    @Query("SELECT * FROM user_recipes WHERE id = :id")
    suspend fun getUserRecipeById(id: Int): UserRecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRecipe(recipe: UserRecipeEntity): Long

    @Update
    suspend fun updateUserRecipe(recipe: UserRecipeEntity)

    @Query("DELETE FROM user_recipes WHERE id = :id")
    suspend fun deleteUserRecipe(id: Int)
}