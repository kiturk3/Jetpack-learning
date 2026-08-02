// RecipeCardTest.kt
package com.kiturk3.recipevault.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kiturk3.recipevault.RecipeCard
import com.kiturk3.recipevault.ui.theme.RecipeVaultTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun recipeCard_displaysTitle() {
        composeTestRule.setContent {
            RecipeVaultTheme {
                RecipeCard(
                    title = "Spaghetti Carbonara",
                    subtitle = "30 min · Italian",
                    isFav = false,
                    thumbnailUrl = null,
                    onFavToggle = {},
                    onClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Spaghetti Carbonara")
            .assertIsDisplayed()
    }

    @Test
    fun recipeCard_displaysSubtitle() {
        composeTestRule.setContent {
            RecipeVaultTheme {
                RecipeCard(
                    title = "Spaghetti Carbonara",
                    subtitle = "30 min · Italian",
                    isFav = false,
                    thumbnailUrl = null,
                    onFavToggle = {},
                    onClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("30 min · Italian")
            .assertIsDisplayed()
    }

    @Test
    fun recipeCard_favToggle_callsCallback() {
        var toggled = false

        composeTestRule.setContent {
            RecipeVaultTheme {
                RecipeCard(
                    title = "Spaghetti Carbonara",
                    subtitle = "30 min · Italian",
                    isFav = false,
                    thumbnailUrl = null,
                    onFavToggle = { toggled = true },
                    onClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Add to favorites")
            .performClick()

        assertTrue(toggled)
    }

    @Test
    fun recipeCard_onClick_callsCallback() {
        var clicked = false

        composeTestRule.setContent {
            RecipeVaultTheme {
                RecipeCard(
                    title = "Spaghetti Carbonara",
                    subtitle = "30 min · Italian",
                    isFav = false,
                    thumbnailUrl = null,
                    onFavToggle = {},
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Spaghetti Carbonara")
            .performClick()

        assertTrue(clicked)
    }

    @Test
    fun recipeCard_isFav_true_showsFilledHeart() {
        composeTestRule.setContent {
            RecipeVaultTheme {
                RecipeCard(
                    title = "Spaghetti Carbonara",
                    subtitle = "30 min · Italian",
                    isFav = true,
                    thumbnailUrl = null,
                    onFavToggle = {},
                    onClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Remove from favorites")
            .assertIsDisplayed()
    }
}