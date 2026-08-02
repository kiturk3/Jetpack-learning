// SearchUITest.kt
package com.kiturk3.recipevault.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kiturk3.recipevault.SearchUI
import com.kiturk3.recipevault.ui.theme.RecipeVaultTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchUI_displaysLabel() {
        composeTestRule.setContent {
            RecipeVaultTheme {
                SearchUI(searchInput = "", onQueryChange = {})
            }
        }

        composeTestRule
            .onNodeWithText("Search")
            .assertIsDisplayed()
    }

    @Test
    fun searchUI_typingCallsOnQueryChange() {
        var query = ""

        composeTestRule.setContent {
            RecipeVaultTheme {
                SearchUI(
                    searchInput = query,
                    onQueryChange = { query = it }
                )
            }
        }

        composeTestRule
            .onNodeWithTag("search_field")
            .performTextInput("chicken")

        assert(query == "chicken")
    }

    @Test
    fun searchUI_clearButton_appearsWhenTextEntered() {
        composeTestRule.setContent {
            var query by remember { mutableStateOf("") }
            RecipeVaultTheme {
                SearchUI(
                    searchInput = query,
                    onQueryChange = { query = it }
                )
            }
        }

        // Clear button not visible initially
        composeTestRule
            .onNodeWithContentDescription("Clear search")
            .assertDoesNotExist()

        // Type something
        composeTestRule
            .onNodeWithTag("search_field")
            .performTextInput("pad")

        // Clear button now visible
        composeTestRule
            .onNodeWithContentDescription("Clear search")
            .assertIsDisplayed()
    }

    @Test
    fun searchUI_clearButton_clearsText() {
        composeTestRule.setContent {
            var query by remember { mutableStateOf("chicken") }
            RecipeVaultTheme {
                SearchUI(
                    searchInput = query,
                    onQueryChange = { query = it }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Clear search")
            .performClick()

        composeTestRule
            .onNodeWithTag("search_field")
            .assertTextContains("")
    }
}