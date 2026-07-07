package com.kiturk3.recipevault.data.remote

import com.kiturk3.recipevault.data.remote.dto.MealsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {
    @GET("search.php")
    suspend fun searchMeals(
        @Query("s") query: String = ""
    ): MealsResponse
}