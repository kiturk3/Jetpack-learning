package com.kiturk3.recipevault.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val mealId: String,
    val title: String,
    val cuisine: String,
    val thumbnailUrl: String?
)
