package com.kiturk3.recipevault.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.usecase.GetRecipeByIdUseCase
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
    savedStateHandle: SavedStateHandle
) : ViewModel(){

    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        val route = savedStateHandle.toRoute<RecipeDetailRoute>()
        loadRecipe(route.recipeId)
    }

    private fun loadRecipe(id: Int) {
        viewModelScope.launch {
            getRecipeByIdUseCase(id)
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _uiState.value = RecipeDetailUiState.Loading
                        is Resource.Success -> _uiState.value = RecipeDetailUiState.Success(resource.data)
                        is Resource.Error -> {
                            if (resource.data != null) {
                                // Have cached recipe — show it
                                _uiState.value = RecipeDetailUiState.Success(resource.data)
                            } else {
                                _uiState.value = RecipeDetailUiState.Error(
                                    resource.message
                                )
                            }
                        }
                    }
                }
        }
    }

}