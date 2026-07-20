package com.kiturk3.recipevault.viewModel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kiturk3.recipevault.domain.usecase.GetRecipesUseCase
import com.kiturk3.recipevault.domain.usecase.SearchRecipesUseCase
import com.kiturk3.recipevault.domain.usecase.ToggleFavoriteUseCase
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.fake.FakeRecipeRepository
import com.kiturk3.recipevault.uiStates.RecipeUiState
import com.kiturk3.recipevault.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: RecipeViewModel
    private lateinit var repository: FakeRecipeRepository

    private val testRecipes = listOf(
        Recipe(1, "Spaghetti Carbonara", 30, "Italian"),
        Recipe(2, "Chicken Tikka Masala", 45, "Indian"),
        Recipe(3, "Pad Thai", 25, "Thai")
    )

    @Before
    fun setUp() {
        repository = FakeRecipeRepository()
        repository.setRecipes(testRecipes)

        viewModel = RecipeViewModel(
            getRecipesUseCase = GetRecipesUseCase(repository),
            getToggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
            searchRecipesUseCase = SearchRecipesUseCase(repository)
        )
    }

    @Test
    fun `initial state is Loading`() = runTest {
        // ViewModel init launches coroutines — before they run, state is Loading
        assertThat(viewModel.uiState.value).isEqualTo(RecipeUiState.Loading)
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(RecipeUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loads recipes successfully on init`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(RecipeUiState.Loading)
            advanceUntilIdle()   // ← run all pending coroutines
            val success = awaitItem() as RecipeUiState.Success
            assertThat(success.recipes).hasSize(3)
            assertThat(success.recipes[0].title).isEqualTo("Spaghetti Carbonara")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits Error state when repository fails`() = runTest {
        repository.shouldReturnError = true
        repository.errorMessage = "Network unavailable"

        val errorViewModel = RecipeViewModel(
            getRecipesUseCase = GetRecipesUseCase(repository),
            getToggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
            searchRecipesUseCase = SearchRecipesUseCase(repository)
        )

        errorViewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(RecipeUiState.Loading)
            advanceUntilIdle()
            val error = awaitItem() as RecipeUiState.Error
            assertThat(error.message).isEqualTo("Network unavailable")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite updates isFav on correct recipe only`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Loading
            advanceUntilIdle()
            val initial = awaitItem() as RecipeUiState.Success
            assertThat(initial.recipes[0].isFav).isFalse()

            viewModel.toggleFavorite(1)
            advanceUntilIdle()

            val updated = awaitItem() as RecipeUiState.Success
            assertThat(updated.recipes.find { it.id == 1 }?.isFav).isTrue()
            assertThat(updated.recipes.find { it.id == 2 }?.isFav).isFalse()
            assertThat(updated.recipes.find { it.id == 3 }?.isFav).isFalse()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retry resets to Loading then reloads`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Initial Loading
            advanceUntilIdle()
            awaitItem() // Success

            viewModel.retry()
            assertThat(awaitItem()).isEqualTo(RecipeUiState.Loading)
            advanceUntilIdle()
            val success = awaitItem() as RecipeUiState.Success
            assertThat(success.recipes).hasSize(3)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search query filters recipes`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Loading
            advanceUntilIdle()
            awaitItem() // Success with all recipes

            viewModel.onSearchQueryChange("chicken")
            // Advance past debounce (300ms)
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(350)
            advanceUntilIdle()

            val searched = awaitItem() as RecipeUiState.Success
            assertThat(searched.recipes).hasSize(1)
            assertThat(searched.recipes[0].title).isEqualTo("Chicken Tikka Masala")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearing search returns full list`() = runTest {
        viewModel.uiState.test {
            awaitItem() // Loading
            advanceUntilIdle()
            awaitItem() // Success

            // Search for something
            viewModel.onSearchQueryChange("chicken")
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(350)
            advanceUntilIdle()
            awaitItem() // Filtered results

            // Clear search
            viewModel.onSearchQueryChange("")
            mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(350)
            advanceUntilIdle()

            val cleared = awaitItem() as RecipeUiState.Success
            assertThat(cleared.recipes).hasSize(3)
            cancelAndIgnoreRemainingEvents()
        }
    }
}