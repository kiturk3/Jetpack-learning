package com.kiturk3.recipevault


import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.kiturk3.recipevault.domain.model.Recipe
import com.kiturk3.recipevault.presentation.auth.AuthStateHandler
import com.kiturk3.recipevault.presentation.components.ShimmerRecipeList
import com.kiturk3.recipevault.route.FavoritesRoute
import com.kiturk3.recipevault.route.LoginRoute
import com.kiturk3.recipevault.route.ProfileRoute
import com.kiturk3.recipevault.route.RecipeListRoute
import com.kiturk3.recipevault.route.RecipeVaultNavHost
import com.kiturk3.recipevault.route.SignupRoute
import com.kiturk3.recipevault.ui.theme.RecipeVaultTheme
import com.kiturk3.recipevault.uiStates.RecipeUiState
import com.kiturk3.recipevault.viewModel.AuthViewModel
import com.kiturk3.recipevault.viewModel.RecipeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Runs once — not inside composition
        val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
            RecipeListRoute
        } else {
            LoginRoute
        }

        setContent {
            RecipeVaultTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        val showBottomBar =
                            currentDestination?.hasRoute(LoginRoute::class) == false &&
                                    currentDestination?.hasRoute(SignupRoute::class) == false
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Recipes") },
                                    label = { Text("Recipes") },
                                    selected = currentDestination?.hasRoute(RecipeListRoute::class) == true,
                                    onClick = {
                                        navController.navigate(RecipeListRoute) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                                    label = { Text("Favorites") },
                                    selected = currentDestination?.hasRoute(FavoritesRoute::class) == true,
                                    onClick = {
                                        navController.navigate(FavoritesRoute) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                    label = { Text("Profile") },
                                    selected = currentDestination?.hasRoute(ProfileRoute::class) == true,
                                    onClick = {
                                        navController.navigate(ProfileRoute) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        RecipeVaultNavHost(
                            navController = navController,
                            startDestination = startDestination,
                            authViewModel = authViewModel
                        )
                        AuthStateHandler(
                            navController = navController,
                            viewModel = authViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavButton(
    isFav: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onToggle, modifier = modifier) {
        Icon(
            if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            null,
            tint = if (isFav) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RecipeScreen(
    onRecipeClick: (Int) -> Unit,
    viewModel: RecipeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by  viewModel.searchQuery.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
        when (uiState) {
            is RecipeUiState.Loading -> {
                ShimmerRecipeList(
                    modifier = Modifier.fillMaxSize()
                )
            }
            is RecipeUiState.Error -> {
                NoRecipeUI(
                    title = "Something went wrong",
                    subtitle = (uiState as RecipeUiState.Error).message,
                    onRetry = { viewModel.retry() }
                )
            }
            is RecipeUiState.Success -> {
                val successState = uiState as RecipeUiState.Success
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchUI(
                        searchInput = searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier.padding(16.dp)
                    )
                    if (isSearching) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                    if (successState.isStale) {
                        Text(
                            text = "Showing cached results — check your connection",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    Text(
                        text = if (successState.searchQuery.isEmpty()) "Total Recipes: ${successState.recipes.size}" else "Matches found: ${successState.filteredRecipes.size}",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    when {
                        successState.recipes.isEmpty() -> NoRecipeUI(
                            title = "No recipes yet",
                            subtitle = "Pull to refresh or check back later"
                        )

                        successState.filteredRecipes.isEmpty() -> NoRecipeUI(
                            title = "No matches found",
                            subtitle = "Try a different search term"
                        )



                        else -> RecipeList(
                            recipes = successState.filteredRecipes,
                            onToggleFav = { viewModel.toggleFavorite(it) },
                            onRecipeClick = onRecipeClick
                        )
                    }
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun RecipeScreenPreview() {
    RecipeVaultTheme {
        RecipeScreen(onRecipeClick = {})
    }
}

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    onToggleFav: (Int) -> Unit,
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(
                title = recipe.title,
                subtitle = "${recipe.duration} min · ${recipe.cuisine}",
                isFav = recipe.isFav,
                onFavToggle = { onToggleFav(recipe.id) },
                onClick = { onRecipeClick(recipe.id) },   // ← no NavController here at all
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun RecipeCard(
    title: String,
    subtitle: String,
    isFav: Boolean,
    onFavToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                FavButton(isFav = isFav, onToggle = onFavToggle)
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
fun NoRecipeUI(
    title: String,
    subtitle: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onRetry) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Try Again")
                }
            }
        }
    }
}

@Composable
fun SearchUI(
    searchInput: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchInput,
        onValueChange = onQueryChange,
        label = { Text("Search", color = MaterialTheme.colorScheme.tertiary) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
        },
        trailingIcon = {
            if (searchInput.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.tertiary,
            unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            unfocusedLabelColor = MaterialTheme.colorScheme.tertiary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.tertiary,
            focusedTrailingIconColor = MaterialTheme.colorScheme.tertiary,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = MaterialTheme.colorScheme.tertiary
        )
    )
}