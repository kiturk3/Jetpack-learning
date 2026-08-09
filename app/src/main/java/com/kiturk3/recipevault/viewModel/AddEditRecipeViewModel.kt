package com.kiturk3.recipevault.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.usecase.AddUserRecipeUseCase
import com.kiturk3.recipevault.domain.usecase.DeleteUserRecipeUseCase
import com.kiturk3.recipevault.domain.usecase.UpdateUserRecipeUseCase
import com.kiturk3.recipevault.route.AddEditRecipeRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditRecipeViewModel @Inject constructor(
    private val addUserRecipeUseCase: AddUserRecipeUseCase,
    private val updateUserRecipeUseCase: UpdateUserRecipeUseCase,
    private val deleteUserRecipeUseCase: DeleteUserRecipeUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: Int? = savedStateHandle
        .toRoute<AddEditRecipeRoute>().recipeId

    val isEditMode = recipeId != null

    // Form state
    var title by mutableStateOf("")
        private set
    var cuisine by mutableStateOf("")
        private set
    var duration by mutableStateOf("")
        private set
    var ingredients by mutableStateOf("")
        private set
    var instructions by mutableStateOf("")
        private set

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun onTitleChange(value: String) { title = value }
    fun onCuisineChange(value: String) { cuisine = value }
    fun onDurationChange(value: String) { duration = value }
    fun onIngredientsChange(value: String) { ingredients = value }
    fun onInstructionsChange(value: String) { instructions = value }

    fun saveRecipe() {
        if (title.isBlank()) {
            _error.value = "Title is required"
            return
        }

        val recipe = Recipe(
            id = recipeId ?: 0,
            title = title.trim(),
            cuisine = cuisine.trim().ifBlank { "Unknown" },
            duration = duration.trim().toIntOrNull() ?: 0,
            ingredients = ingredients.trim(),
            instructions = instructions.trim(),
            isUserCreated = true
        )

        viewModelScope.launch {
            try {
                if (isEditMode) {
                    updateUserRecipeUseCase(recipe)
                } else {
                    addUserRecipeUseCase(recipe)
                }
                _isSaved.value = true
            } catch (e: Exception) {
                _error.value = "Failed to save: ${e.message}"
            }
        }
    }

    fun deleteRecipe() {
        val id = recipeId ?: return
        viewModelScope.launch {
            try {
                deleteUserRecipeUseCase(id)
                _isSaved.value = true
            } catch (e: Exception) {
                _error.value = "Failed to delete: ${e.message}"
            }
        }
    }

    fun clearError() { _error.value = null }

}