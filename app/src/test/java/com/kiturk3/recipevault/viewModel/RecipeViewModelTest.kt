package com.kiturk3.recipevault.viewModel

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
    }

    @Test
    fun `initial state is Loading`() = runTest {
        val viewModel = createViewModel()
        assertThat(viewModel.uiState.value).isEqualTo(RecipeUiState.Loading)
    }

    @Test
    fun `loads recipes successfully on init`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val state = viewModel.uiState.value as RecipeUiState.Success
        assertThat(state.recipes).hasSize(3)
        assertThat(state.recipes[0].title).isEqualTo("Spaghetti Carbonara")
    }

    @Test
    fun `emits Error state when repository fails`() = runTest {
        repository.shouldReturnError = true
        repository.errorMessage = "Network unavailable"
        val viewModel = createViewModel()
        advanceUntilIdle()
        val state = viewModel.uiState.value as RecipeUiState.Error
        assertThat(state.message).isEqualTo("Network unavailable")
    }

    @Test
    fun `toggleFavorite updates isFav on correct recipe only`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertThat((viewModel.uiState.value as RecipeUiState.Success).recipes[0].isFav).isFalse()

        viewModel.toggleFavorite(1)
        advanceUntilIdle()

        val updated = viewModel.uiState.value as RecipeUiState.Success
        assertThat(updated.recipes.find { it.id == 1 }?.isFav).isTrue()
        assertThat(updated.recipes.find { it.id == 2 }?.isFav).isFalse()
        assertThat(updated.recipes.find { it.id == 3 }?.isFav).isFalse()
    }

    @Test
    fun `retry resets to Loading then reloads`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle() // init completes → Success

        viewModel.retry()
        assertThat(viewModel.uiState.value).isEqualTo(RecipeUiState.Loading)

        advanceUntilIdle()
        val success = viewModel.uiState.value as RecipeUiState.Success
        assertThat(success.recipes).hasSize(3)
    }

    @Test
    fun `search query filters recipes`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("chicken")
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value as RecipeUiState.Success
        assertThat(state.recipes).hasSize(1)
        assertThat(state.recipes[0].title).isEqualTo("Chicken Tikka Masala")
    }

    @Test
    fun `clearing search returns full list`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("chicken")
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(350)
        advanceUntilIdle()

        viewModel.onSearchQueryChange("")
        mainDispatcherRule.testDispatcher.scheduler.advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value as RecipeUiState.Success
        assertThat(state.recipes).hasSize(3)
    }

    private fun createViewModel() = RecipeViewModel(
        getRecipesUseCase = GetRecipesUseCase(repository),
        getToggleFavoriteUseCase = ToggleFavoriteUseCase(repository),
        searchRecipesUseCase = SearchRecipesUseCase(repository)
    )
}

