package com.kiturk3.recipevault.uiStates

sealed class AuthNavigationEvent {
    data object NavigateToLogin: AuthNavigationEvent()
    data object NavigateToHome: AuthNavigationEvent()
}