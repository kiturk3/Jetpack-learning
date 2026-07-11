package com.kiturk3.recipevault.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kiturk3.recipevault.RecipeCard
import com.kiturk3.recipevault.uiStates.FavoritesUiState
import com.kiturk3.recipevault.viewModel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    onRecipeClick: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
    ) {
        val  uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        when (val state = uiState) {
            is FavoritesUiState.Loading -> {
                Text("Loading favourites...")
            }

            is FavoritesUiState.Empty -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "No favorites yet",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the heart on any recipe to save it here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is FavoritesUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "My Favorites (${state.favorites.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(state.favorites, key = { it.id }) { recipe ->
                        RecipeCard(
                            title = recipe.title,
                            subtitle = "${recipe.duration} min · ${recipe.cuisine}",
                            isFav = true,
                            onFavToggle = { viewModel.removeFavourites(recipe.id) },
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }

        }
    }

}