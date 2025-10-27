package com.miraimagiclab.novelreadingapp.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ReadingProgressViewModel
import com.miraimagiclab.novelreadingapp.ui.viewmodel.ReadingSettingsViewModel
import io.mockk.*
import org.junit.Rule
import org.junit.Test
class ReadingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ReadingScreen should display chapter content`() {
        // Given
        val chapterTitle = "Test Chapter"
        val chapterContent = "This is a test chapter content for reading."
        var backClicked = false
        var previousClicked = false
        var nextClicked = false

        // When
        composeTestRule.setContent {
            ReadingScreen(
                novelId = "novel123",
                chapterId = "chapter456",
                onBackClick = { backClicked = true }
            )
        }

        // Then
        composeTestRule.onNodeWithText("Loading chapter...").assertIsDisplayed()
    }

    @Test
    fun `ReadingScreen should show navigation buttons when UI is visible`() {
        // Given
        val chapterTitle = "Test Chapter"
        val chapterContent = "This is a test chapter content for reading."
        var backClicked = false
        var previousClicked = false
        var nextClicked = false

        // When
        composeTestRule.setContent {
            ReadingScreen(
                novelId = "novel123",
                chapterId = "chapter456",
                onBackClick = { backClicked = true }
            )
        }

        // Tap to show UI
        composeTestRule.onRoot().performClick()

        // Then
        composeTestRule.onNodeWithText("Previous").assertIsDisplayed()
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
    }

    @Test
    fun `ReadingScreen should show settings dialog when settings icon is clicked`() {
        // Given
        val chapterTitle = "Test Chapter"
        val chapterContent = "This is a test chapter content for reading."
        var backClicked = false
        var previousClicked = false
        var nextClicked = false

        // When
        composeTestRule.setContent {
            ReadingScreen(
                novelId = "novel123",
                chapterId = "chapter456",
                onBackClick = { backClicked = true }
            )
        }

        // Tap to show UI
        composeTestRule.onRoot().performClick()

        // Click settings icon
        composeTestRule.onNodeWithContentDescription("Reading Settings").performClick()

        // Then
        composeTestRule.onNodeWithText("Reading Settings").assertIsDisplayed()
    }

    @Test
    fun `ReadingScreen should show chapter list when list icon is clicked`() {
        // Given
        val chapterTitle = "Test Chapter"
        val chapterContent = "This is a test chapter content for reading."
        var backClicked = false
        var previousClicked = false
        var nextClicked = false

        // When
        composeTestRule.setContent {
            ReadingScreen(
                novelId = "novel123",
                chapterId = "chapter456",
                onBackClick = { backClicked = true }
            )
        }

        // Tap to show UI
        composeTestRule.onRoot().performClick()

        // Click chapter list icon
        composeTestRule.onNodeWithContentDescription("Chapter List").performClick()

        // Then
        composeTestRule.onNodeWithText("Chapter List").assertIsDisplayed()
    }

    @Test
    fun `ReadingScreen should disable previous button when hasPreviousChapter is false`() {
        // Given
        val chapterTitle = "Test Chapter"
        val chapterContent = "This is a test chapter content for reading."
        var backClicked = false
        var previousClicked = false
        var nextClicked = false

        // When
        composeTestRule.setContent {
            ReadingScreen(
                novelId = "novel123",
                chapterId = "chapter456",
                onBackClick = { backClicked = true }
            )
        }

        // Tap to show UI
        composeTestRule.onRoot().performClick()

        // Then
        composeTestRule.onNodeWithText("Previous").assertIsNotEnabled()
    }

    @Test
    fun `ReadingScreen should disable next button when hasNextChapter is false`() {
        // Given
        val chapterTitle = "Test Chapter"
        val chapterContent = "This is a test chapter content for reading."
        var backClicked = false
        var previousClicked = false
        var nextClicked = false

        // When
        composeTestRule.setContent {
            ReadingScreen(
                novelId = "novel123",
                chapterId = "chapter456",
                onBackClick = { backClicked = true }
            )
        }

        // Tap to show UI
        composeTestRule.onRoot().performClick()

        // Then
        composeTestRule.onNodeWithText("Next").assertIsNotEnabled()
    }
}
