// GetRecipesUseCaseTest.kt
package com.kiturk3.recipevault.domain.usecase

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.fake.FakeRecipeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetRecipesUseCaseTest {

    private lateinit var useCase: GetRecipesUseCase
    private lateinit var repository: FakeRecipeRepository

    @Before
    fun setUp() {
        repository = FakeRecipeRepository()
        useCase = GetRecipesUseCase(repository)
    }

    @Test
    fun `returns loading then success with recipes`(): Unit = runTest {
        val recipes = listOf(
            Recipe(1, "Spaghetti Carbonara", 30, "Italian"),
            Recipe(2, "Pad Thai", 25, "Thai")
        )
        repository.setRecipes(recipes)

        useCase().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            val success = awaitItem()
            assertThat(success).isInstanceOf(Resource.Success::class.java)
            assertThat((success as Resource.Success).data).hasSize(2)
            assertThat(success.data[0].title).isEqualTo("Spaghetti Carbonara")
            awaitComplete()
        }
    }

    @Test
    fun `returns loading then error when repository fails`(): Unit = runTest {
        repository.shouldReturnError = true
        repository.errorMessage = "Network error"

        useCase().test {
            assertThat(awaitItem()).isInstanceOf(Resource.Loading::class.java)
            val error = awaitItem()
            assertThat(error).isInstanceOf(Resource.Error::class.java)
            assertThat((error as Resource.Error).message).isEqualTo("Network error")
            awaitComplete()
        }
    }

    @Test
    fun `returns empty list when no recipes exist`(): Unit = runTest {
        repository.setRecipes(emptyList())

        useCase().test {
            awaitItem() // Loading
            val success = awaitItem() as Resource.Success
            assertThat(success.data).isEmpty()
            awaitComplete()
        }
    }
}