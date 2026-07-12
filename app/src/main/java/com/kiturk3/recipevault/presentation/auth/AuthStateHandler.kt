package com.kiturk3.recipevault.presentation.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.kiturk3.recipevault.domain.model.User
import com.kiturk3.recipevault.route.LoginRoute
import com.kiturk3.recipevault.route.RecipeListRoute
import com.kiturk3.recipevault.uiStates.AuthNavigationEvent
import com.kiturk3.recipevault.viewModel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun AuthStateHandler(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAuthChecked by viewModel.isAuthChecked.collectAsStateWithLifecycle()
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser, isAuthChecked) {
        Log.d("AuthHandler", "currentUser=$currentUser, isAuthChecked=$isAuthChecked")
        if (isAuthChecked && currentUser == null) {
            Log.d("AuthHandler", "Navigating to login")
            navController.navigate(LoginRoute) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is AuthNavigationEvent.NavigateToLogin -> {
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is AuthNavigationEvent.NavigateToHome -> {
                    navController.navigate(RecipeListRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            }
        }
    }
}