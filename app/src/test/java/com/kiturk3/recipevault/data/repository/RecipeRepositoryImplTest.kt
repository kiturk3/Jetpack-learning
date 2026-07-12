// RecipeRepositoryImplTest.kt
package com.kiturk3.recipevault.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kiturk3.recipevault.data.local.dao.FavoriteDao
import com.kiturk3.recipevault.data.local.dao.RecipeDao
import com.kiturk3.recipevault.data.local.entity.RecipeEntity
import com.kiturk3.recipevault.data.remote.MealApiService
import com.kiturk3.recipevault.data.remote.dto.MealDto
import com.kiturk3.recipevault.data.remote.dto.MealsResponse
import com.kiturk3.recipevault.domain.Resource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RecipeRepositoryImplTest {

    private lateinit var repository: RecipeRepositoryImpl
    private val apiService: MealApiService = mockk()
    private val favoriteDao: FavoriteDao = mockk()
    private val recipeDao: RecipeDao = mockk()

    private val testEntity = RecipeEntity(
        mealId = "1",
        title = "Spaghetti Carbonara",
        cuisine = "Italian",
        thumbnailUrl = null,
        instructions = null
    )

    private val testDto = MealDto(
        id = "1",
        name = "Spaghetti Carbonara",
        category = "Pasta",
        area = "Italian",
        thumbnailUrl = null,
        instructions = null
    )

    @Before
    fun setUp() {
        // Default DAO behavior
        every { favoriteDao.getFavoriteIds() } returns flowOf(emptyList())
        every { recipeDao.getAllRecipes() } returns flowOf(emptyList())
        every { recipeDao.getRecipeById(any()) } returns flowOf(null)

        repository = RecipeRepositoryImpl(apiService, favoriteDao, recipeDao)
    }

    @Test
    fun `getRecipes emits success with mapped recipes from API`() = runTest {
        every { recipeDao.getAllRecipes() } returns flowOf(emptyList())
        coEvery { apiService.searchMeals(any()) } returns MealsResponse(listOf(testDto))
        coEvery { recipeDao.clearRecipes() } returns Unit
        coEvery { recipeDao.insertRecipes(any()) } returns Unit

        repository.getRecipes().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            val success = awaitItem() as Resource.Success
            assertThat(success.data).hasSize(1)
            assertThat(success.data[0].title).isEqualTo("Spaghetti Carbonara")
            awaitComplete()
        }
    }

    @Test
    fun `getRecipes emits cached data first when cache is not empty`() = runTest {
        every { recipeDao.getAllRecipes() } returns flowOf(listOf(testEntity))
        coEvery { apiService.searchMeals(any()) } returns MealsResponse(listOf(testDto))
        coEvery { recipeDao.clearRecipes() } returns Unit
        coEvery { recipeDao.insertRecipes(any()) } returns Unit

        repository.getRecipes().test {
            awaitItem() // Loading
            val cached = awaitItem() as Resource.Success  // cached emission
            assertThat(cached.data[0].title).isEqualTo("Spaghetti Carbonara")
            val fresh = awaitItem() as Resource.Success   // network emission
            assertThat(fresh.data[0].title).isEqualTo("Spaghetti Carbonara")
            awaitComplete()
        }
    }

    @Test
    fun `getRecipes emits error when API fails and cache is empty`() = runTest {
        every { recipeDao.getAllRecipes() } returns flowOf(emptyList())
        coEvery { apiService.searchMeals(any()) } throws Exception("Network error")

        repository.getRecipes().test {
            awaitItem() // Loading
            val error = awaitItem() as Resource.Error
            assertThat(error.message).isEqualTo("Network error")
            awaitComplete()
        }
    }

    @Test
    fun `getRecipes marks favorites correctly from local DB`() = runTest {
        every { favoriteDao.getFavoriteIds() } returns flowOf(listOf("1"))
        every { recipeDao.getAllRecipes() } returns flowOf(emptyList())
        coEvery { apiService.searchMeals(any()) } returns MealsResponse(listOf(testDto))
        coEvery { recipeDao.clearRecipes() } returns Unit
        coEvery { recipeDao.insertRecipes(any()) } returns Unit

        repository.getRecipes().test {
            awaitItem() // Loading
            val success = awaitItem() as Resource.Success
            assertThat(success.data[0].isFav).isTrue()
            awaitComplete()
        }
    }
}