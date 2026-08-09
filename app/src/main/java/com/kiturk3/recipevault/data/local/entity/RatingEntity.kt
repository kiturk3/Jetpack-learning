package com.kiturk3.recipevault.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ratings")
data class RatingEntity(
    @PrimaryKey val recipeId: String,  // "api_52772" or "user_1"
    val rating: Int,                   // 1-5
    val notes: String = ""
)