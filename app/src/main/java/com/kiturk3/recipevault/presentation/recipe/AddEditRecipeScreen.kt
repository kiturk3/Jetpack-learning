package com.kiturk3.recipevault.presentation.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kiturk3.recipevault.viewModel.AddEditRecipeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay

@Composable
fun AddEditRecipeScreen(
    onBack: () -> Unit,
    viewModel: AddEditRecipeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val isSaved by viewModel.isSaved.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(isSaved) {
        if (isSaved) onBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (viewModel.isEditMode) "Edit Recipe" else "Add Recipe",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            if (viewModel.isEditMode) {
                IconButton(onClick = { viewModel.deleteRecipe() }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete recipe",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Error banner
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            LaunchedEffect(it) {
                delay(3000)
                viewModel.clearError()
            }
        }

        // Form fields
        OutlinedTextField(
            value = viewModel.title,
            onValueChange = viewModel::onTitleChange,
            label = { Text("Recipe Title *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = viewModel.cuisine,
            onValueChange = viewModel::onCuisineChange,
            label = { Text("Cuisine") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = viewModel.duration,
            onValueChange = viewModel::onDurationChange,
            label = { Text("Duration (minutes)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = viewModel.ingredients,
            onValueChange = viewModel::onIngredientsChange,
            label = { Text("Ingredients (one per line)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 8
        )

        OutlinedTextField(
            value = viewModel.instructions,
            onValueChange = viewModel::onInstructionsChange,
            label = { Text("Instructions") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 12
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = viewModel::saveRecipe,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(if (viewModel.isEditMode) "Update Recipe" else "Save Recipe")
        }

        if (viewModel.isEditMode) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}