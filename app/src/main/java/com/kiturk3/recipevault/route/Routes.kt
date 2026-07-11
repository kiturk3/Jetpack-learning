package com.kiturk3.recipevault.route

import kotlinx.serialization.Serializable

@Serializable
object RecipeListRoute

@Serializable
data class RecipeDetailRoute(val recipeId: Int)

@Serializable
object FavoritesRoute

@Serializable
data class AddEditRecipeRoute(val recipeId: Int? = null)
