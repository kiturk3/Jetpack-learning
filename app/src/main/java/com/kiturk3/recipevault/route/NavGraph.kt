package com.kiturk3.recipevault.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kiturk3.recipevault.LoginScreen
import com.kiturk3.recipevault.RecipeDetailScreen
import com.kiturk3.recipevault.RecipeScreen
import com.kiturk3.recipevault.SignupScreen
import com.kiturk3.recipevault.presentation.FavoritesScreen

@Composable
fun RecipeVaultNavHost(navController: NavHostController,
                       startDestination: Any = LoginRoute) {
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
    }
}