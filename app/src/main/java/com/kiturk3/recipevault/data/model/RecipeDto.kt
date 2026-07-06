package com.kiturk3.recipevault.data.model

data class RecipeDto(
    val id: Int,
    val title: String,
    val readyInMinutes: Int,
    val cuisines: List<String>
)