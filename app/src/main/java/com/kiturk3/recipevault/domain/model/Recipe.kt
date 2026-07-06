package com.kiturk3.recipevault.domain.model

data class Recipe(
    val id: Int,
    val title: String,
    val duration: Int,
    val cuisine: String,
    val isFav: Boolean = false
)
