package com.kiturk3.recipevault.data.remote.mapper

import com.kiturk3.recipevault.data.remote.dto.MealDto
import com.kiturk3.recipevault.domain.model.Recipe

fun MealDto.toRecipe(): Recipe{
    return Recipe(
        id = id.toIntOrNull() ?: 0,
        title = name,
        duration = 0,
        cuisine = area ?: category ?: "Unknown",
        isFav = false,
    )
}