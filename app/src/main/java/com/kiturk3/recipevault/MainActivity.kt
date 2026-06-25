package com.kiturk3.recipevault


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiturk3.recipevault.model.RecipeItem
import com.kiturk3.recipevault.ui.theme.RecipeVaultTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeVaultTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        RecipeScreen()
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
        Icon(if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, null)
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var recipes by remember { mutableStateOf(listOf(
        RecipeItem(1, "Spaghetti Carbonara", "30 min · Italian", false),
        RecipeItem(2, "Chicken Tikka Masala", "45 min · Indian", false),
        RecipeItem(3, "Pad Thai", "25 min · Thai", false)))}

    fun toggleFavorite(id: Int) {
        recipes = recipes.map { item ->
            if (item.id == id) item.copy(isFav = !item.isFav) else item
        }
    }

    val filteredRecipes = recipes.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.durationAndCuisine.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = modifier) {
        SearchUI(searchInput = searchQuery, onQueryChange = { searchQuery = it })
        RecipeList(filteredRecipes, onToggleFav = { toggleFavorite(it) })
    }

}


@Composable
fun RecipeList(
    recipes: List<RecipeItem>,
    onToggleFav: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = recipes,
            key = { it.id }
        ) { recipe ->
            RecipeCard(
                title = recipe.title,
                subtitle = recipe.durationAndCuisine,
                isFav = recipe.isFav,
                onFavToggle = { onToggleFav(recipe.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SearchUI(
    searchInput: String,
    onQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = searchInput,
        onValueChange = onQueryChange,
        label = {Text("Search")},
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun RecipeCard(
    title : String,
    subtitle: String,
    isFav : Boolean,
    onFavToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$30",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.Bold
                    )
                    FavButton(isFav = isFav, onToggle = onFavToggle)
                }

            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}