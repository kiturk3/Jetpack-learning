package com.kiturk3.recipevault.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kiturk3.recipevault.RecipeDetailScreen
import com.kiturk3.recipevault.RecipeScreen

@Composable
fun RecipeVaultNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = RecipeListRoute
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
            RecipeDetailScreen()
        }
    }
}