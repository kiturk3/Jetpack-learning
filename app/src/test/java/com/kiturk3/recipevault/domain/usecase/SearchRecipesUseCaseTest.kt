// SearchRecipesUseCaseTest.kt
package com.kiturk3.recipevault.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.fake.FakeRecipeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SearchRecipesUseCaseTest {

    private lateinit var useCase: SearchRecipesUseCase
    private lateinit var repository: FakeRecipeRepository

    @Before
    fun setUp() {
        repository = FakeRecipeRepository()
        useCase = SearchRecipesUseCase(repository)

        repository.setRecipes(listOf(
            Recipe(1, "Spaghetti Carbonara", 30, "Italian"),
            Recipe(2, "Chicken Tikka Masala", 45, "Indian"),
            Recipe(3, "Pad Thai", 25, "Thai")
        ))
    }

    @Test
    fun `returns matching recipes for query`() = runTest {
        useCase("chicken").test {
            awaitItem() // Loading
            val success = awaitItem() as Resource.Success
            assertThat(success.data).hasSize(1)
            assertThat(success.data[0].title).isEqualTo("Chicken Tikka Masala")
            awaitComplete()
        }
    }

    @Test
    fun `returns empty list for no matches`() = runTest {
        useCase("xyz123").test {
            awaitItem() // Loading
            val success = awaitItem() as Resource.Success
            assertThat(success.data).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `search is case insensitive`() = runTest {
        useCase("CHICKEN").test {
            awaitItem() // Loading
            val success = awaitItem() as Resource.Success
            assertThat(success.data).hasSize(1)
            awaitComplete()
        }
    }
}