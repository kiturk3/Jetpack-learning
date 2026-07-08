package com.kiturk3.recipevault.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MealDto(
    @SerializedName("idMeal") val id: String,
    @SerializedName("strMeal") val name: String,
    @SerializedName("strCategory") val category: String?,
    @SerializedName("strArea") val area: String?,
    @SerializedName("strMealThumb") val thumbnailUrl: String?,
    @SerializedName("strInstructions") val instructions: String?
)

data class MealsResponse(
    @SerializedName("meals") val meals: List<MealDto>?
)