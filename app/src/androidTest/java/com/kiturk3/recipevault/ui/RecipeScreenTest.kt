// RecipeScreenTest.kt
package com.kiturk3.recipevault.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kiturk3.recipevault.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RecipeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun recipeScreen_showsLoadingInitially() {
        // Shimmer shows while loading — check it's visible
        composeTestRule
            .onNodeWithTag("shimmer_list")
            .assertIsDisplayed()
    }

    @Test
    fun recipeScreen_searchField_isVisible() {
        // Wait for loading to complete
        composeTestRule.waitUntil(5000) {
            composeTestRule
                .onAllNodesWithTag("search_field")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithTag("search_field")
            .assertIsDisplayed()
    }
}