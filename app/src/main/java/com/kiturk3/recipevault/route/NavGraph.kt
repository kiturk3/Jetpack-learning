package com.kiturk3.recipevault.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kiturk3.recipevault.presentation.auth.LoginScreen
import com.kiturk3.recipevault.presentation.ProfileScreen
import com.kiturk3.recipevault.presentation.RecipeDetailScreen
import com.kiturk3.recipevault.RecipeScreen
import com.kiturk3.recipevault.presentation.auth.SignupScreen
import com.kiturk3.recipevault.presentation.FavoritesScreen
import com.kiturk3.recipevault.viewModel.AuthViewModel

@Composable
fun RecipeVaultNavHost(navController: NavHostController,
                       startDestination: Any = LoginRoute,
                       authViewModel: AuthViewModel) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<RecipeListRoute> {
            RecipeScreen(
                onRecipeClick = { recipeId ->
                    navController.navigate(RecipeDetailRoute(recipeId))
                }
            )
        }
        composable<RecipeDetailRoute> { backStackEntry ->
            val route: RecipeDetailRoute = backStackEntry.toRoute()
            RecipeDetailScreen(onBack = { navController.popBackStack() })
        }
        composable<FavoritesRoute>{
            FavoritesScreen(onRecipeClick = {recipeId ->
                navController.navigate(RecipeDetailRoute(recipeId))
            })
        }
        composable<LoginRoute>{
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToSignUp = {navController.navigate(SignupRoute)},
                onLoginSuccess = {
                    navController.navigate(RecipeListRoute){
                        popUpTo(LoginRoute){
                            inclusive = true
                        }
                    }
                }
            )

        }
        composable<SignupRoute>{
            SignupScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate(RecipeListRoute){
                        popUpTo(LoginRoute){
                            inclusive = true
                        }
                    }
                }
            )

        }
        composable<ProfileRoute>{
            ProfileScreen(
                viewModel = authViewModel
            )
        }
    }
}