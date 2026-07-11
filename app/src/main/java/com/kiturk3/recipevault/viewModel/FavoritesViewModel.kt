package com.kiturk3.recipevault.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiturk3.recipevault.domain.usecase.GetFavoritesUseCase
import com.kiturk3.recipevault.domain.usecase.ToggleFavoriteUseCase
import com.kiturk3.recipevault.uiStates.FavoritesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
): ViewModel(){

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavourites()
    }

    private fun loadFavourites(){
        viewModelScope.launch {
            getFavoritesUseCase().collect {
                favorites ->
                _uiState.value = if(favorites.isEmpty()){
                    FavoritesUiState.Empty
                }
                else{
                    FavoritesUiState.Success(favorites)
                }
            }
        }
    }

    fun removeFavourites(id: Int){
        viewModelScope.launch {
            toggleFavoriteUseCase(id, false)
        }
    }
}