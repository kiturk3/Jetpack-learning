package com.kiturk3.recipevault.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiturk3.recipevault.domain.Resource
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.domain.usecase.GetRecipesUseCase
import com.kiturk3.recipevault.domain.usecase.RefreshRecipesUseCase
import com.kiturk3.recipevault.domain.usecase.SearchRecipesUseCase
import com.kiturk3.recipevault.domain.usecase.ToggleFavoriteUseCase
import com.kiturk3.recipevault.uiStates.RecipeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val getToggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val refreshRecipesUseCase: RefreshRecipesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    init {
        observeLocalData()    // start reactive observation
        refreshFromNetwork()  // trigger one-time network refresh
        observeSearch()
    }

    private fun observeLocalData() {
        viewModelScope.launch {
            getRecipesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> updateSuccess(resource.data, _searchQuery.value)
                    is Resource.Error -> _uiState.value = RecipeUiState.Error(resource.message)
                    is Resource.Loading -> _uiState.value = RecipeUiState.Loading
                }
            }
        }
    }

    // One-time network refresh — doesn't affect the reactive observer
    private fun refreshFromNetwork() {
        viewModelScope.launch {
            try {
                refreshRecipesUseCase()
                // Room updates automatically → observeLocalData() picks it up
            } catch (e: Exception) {
                // Only show error if we have nothing to display
                if (_uiState.value is RecipeUiState.Loading) {
                    _uiState.value = RecipeUiState.Error(
                        e.message ?: "Failed to load recipes"
                    )
                }
                // Otherwise silent fail — local data already showing
            }
        }
    }

    // Public retry — re-triggers network refresh, not a full reset
    fun retry() {
        if (_uiState.value is RecipeUiState.Error) {
            _uiState.value = RecipeUiState.Loading
        }
        refreshFromNetwork()
    }


    private fun loadRecipes() {
        viewModelScope.launch {
            getRecipesUseCase()
                .collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _uiState.value = RecipeUiState.Loading
                        is Resource.Success -> updateSuccess(resource.data, _searchQuery.value)
                        is Resource.Error -> {
                            if (resource.data != null) {
                                updateSuccess(resource.data, _searchQuery.value, isStale = true)
                            } else {
                                _uiState.value = RecipeUiState.Error(resource.message)
                            }
                        }
                    }
                }
        }
    }

    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300.milliseconds)
                .flatMapLatest { query ->
                    _isSearching.value = true
                    if (query.isBlank()) {
                        getRecipesUseCase()
                    } else {
                        searchRecipesUseCase(query)
                    }
                }
                .collect { resource ->
                    _isSearching.value = false
                    when (resource) {
                        is Resource.Loading -> {}
                        is Resource.Success -> updateSuccess(resource.data, _searchQuery.value)
                        is Resource.Error -> {
                            if (resource.data != null) {
                                updateSuccess(resource.data, _searchQuery.value, true)
                            } else {
                                _uiState.value = RecipeUiState.Error(resource.message)
                            }
                        }
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(id: Int) {
        val current = _uiState.value as? RecipeUiState.Success ?: return
        val recipe = current.recipes.find { it.id == id } ?: return
        viewModelScope.launch {
            getToggleFavoriteUseCase(id, !recipe.isFav)
            getRecipesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {}
                    is Resource.Success -> updateSuccess(resource.data, _searchQuery.value)
                    is Resource.Error -> {
                        if (resource.data != null) {
                            updateSuccess(resource.data, _searchQuery.value, true)
                        }
                    }
                }
            }
        }
    }

    private fun updateSuccess(recipes: List<Recipe>, query: String, isStale: Boolean = false) {
        _uiState.value = RecipeUiState.Success(
            recipes = recipes,
            filteredRecipes = recipes,
            searchQuery = query,
            isStale = isStale
        )
    }
}