package com.kiturk3.recipevault.domain.model

data class Rating(
    val recipeId: String,
    val rating: Int,
    val notes: String = ""
)