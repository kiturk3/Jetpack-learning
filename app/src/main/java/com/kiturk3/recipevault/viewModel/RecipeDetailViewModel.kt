package com.kiturk3.recipevault.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.Rating
import com.kiturk3.recipevault.domain.usecase.GetRatingUseCase
import com.kiturk3.recipevault.domain.usecase.GetRecipeByIdUseCase
import com.kiturk3.recipevault.domain.usecase.UpsertRatingUseCase
import com.kiturk3.recipevault.presentation.detail.RecipeDetailUiState
import com.kiturk3.recipevault.route.RecipeDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val upsertRatingUseCase: UpsertRatingUseCase,
    private val getRatingUseCase: GetRatingUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    private val _rating = MutableStateFlow<Rating?>(null)
    val rating: StateFlow<Rating?> = _rating.asStateFlow()

    private val recipeId: Int

    init {
        val route = savedStateHandle.toRoute<RecipeDetailRoute>()
        recipeId = route.recipeId
        loadRecipe(recipeId)
        loadRating(recipeId)
    }

    private fun loadRecipe(id: Int) {
        viewModelScope.launch {
            getRecipeByIdUseCase(id)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _uiState.value = RecipeDetailUiState.Loading
                        is Resource.Success -> _uiState.value =
                            RecipeDetailUiState.Success(resource.data)

                        is Resource.Error -> {
                            if (resource.data != null) {
                                _uiState.value = RecipeDetailUiState.Success(resource.data)
                            } else {
                                _uiState.value = RecipeDetailUiState.Error(resource.message)
                            }
                        }
                    }
                }
        }
    }

    private fun loadRating(id: Int) {
        val recipeKey = if (
            (uiState.value as? RecipeDetailUiState.Success)?.recipe?.isUserCreated == true
        ) "user_$id" else "api_$id"

        viewModelScope.launch {
            getRatingUseCase(recipeKey).collect { rating ->
                _rating.value = rating
            }
        }
    }

    fun upsertRating(rating: Int, notes: String) {
        val state = uiState.value as? RecipeDetailUiState.Success ?: return
        val key = if (state.recipe.isUserCreated) "user_$recipeId" else "api_$recipeId"
        viewModelScope.launch {
            upsertRatingUseCase(key, rating, notes)
        }
    }

}