package com.kiturk3.recipevault.uiStates

import com.kiturk3.recipevault.domain.model.User

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}