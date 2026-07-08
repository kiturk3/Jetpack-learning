package com.kiturk3.recipevault.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kiturk3.recipevault.domain.usecase.GetRecipeByIdUseCase
import com.kiturk3.recipevault.presentation.detail.RecipeDetailUiState
import com.kiturk3.recipevault.route.RecipeDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    private fun loadRecipe(id : Int){
            viewModelScope.launch {
                getRecipeByIdUseCase(id)
                    .catch { e -> _uiState.value = RecipeDetailUiState.Error(e.message ?: "Failed to load Recipe...") }
                    .collect {
                        recipe ->
                        _uiState.value = if(recipe != null){
                            RecipeDetailUiState.Success(recipe)
                        }
                        else{
                            RecipeDetailUiState.Error("Recipe not found")
                        }
                    }
            }
    }

}