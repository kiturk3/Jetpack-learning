package com.kiturk3.recipevault.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val mealId: String,
    val title: String,
    val cuisine: String,
    val thumbnailUrl: String?,
    val instructions: String?
)