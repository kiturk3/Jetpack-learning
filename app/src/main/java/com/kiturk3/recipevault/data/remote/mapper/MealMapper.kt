package com.kiturk3.recipevault.data.remote.mapper

import com.kiturk3.recipevault.data.local.entity.RecipeEntity
import com.kiturk3.recipevault.data.remote.dto.MealDto
import com.kiturk3.recipevault.domain.model.Recipe

// MealDto → Recipe (direct, no caching)
fun MealDto.toRecipe(): Recipe = Recipe(
    id = id.toIntOrNull() ?: 0,
    title = name,
    duration = 0,
    cuisine = area ?: category ?: "Unknown",
    isFav = false,
    instructions = instructions
)

// MealDto → RecipeEntity (for Room cache)
fun MealDto.toEntity(): RecipeEntity = RecipeEntity(
    mealId = id,
    title = name,
    cuisine = area ?: category ?: "Unknown",
    thumbnailUrl = thumbnailUrl,
    instructions = instructions
)

// RecipeEntity → Recipe (from cache, with favorites cross-reference)
fun RecipeEntity.toRecipe(isFav: Boolean = false): Recipe = Recipe(
    id = mealId.toIntOrNull() ?: 0,
    title = title,
    duration = 0,
    cuisine = cuisine,
    isFav = isFav,
    instructions = instructions
)