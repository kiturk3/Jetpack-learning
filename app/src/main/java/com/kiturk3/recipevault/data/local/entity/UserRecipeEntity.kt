package com.kiturk3.recipevault.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_recipes")
data class UserRecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val cuisine: String,
    val duration: Int,
    val ingredients: String,   // stored as newline-separated string
    val instructions: String,
    val createdAt: Long = System.currentTimeMillis()
)