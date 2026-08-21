package com.desponsor.feature.home

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.desponsor.core.model.Podcast
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun podcast(id: String) = Podcast(id, "Show $id", "Publisher", "desc", null, isSubscribed = true)

    @Test
    fun emptyState_showsGuidanceTowardExplore() {
        composeTestRule.setContent {
            HomeScreen(uiState = HomeUiState.Empty, onPodcastSelected = {})
        }

        composeTestRule.onNodeWithText("Explore", substring = true).assertExists()
    }

    @Test
    fun content_showsSubscribedPodcastTitles() {
        composeTestRule.setContent {
            HomeScreen(uiState = HomeUiState.Content(listOf(podcast("p1"))), onPodcastSelected = {})
        }

        composeTestRule.onNodeWithText("Show p1").assertExists()
    }
}
