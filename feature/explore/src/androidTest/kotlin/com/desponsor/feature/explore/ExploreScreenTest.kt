package com.desponsor.feature.explore

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.desponsor.core.model.Podcast
import org.junit.Rule
import org.junit.Test

class ExploreScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun podcast(
        id: String,
        title: String,
    ) = Podcast(id, title, "Publisher", "desc", null, isSubscribed = false)

    @Test
    fun catalogIsShownWhenNotSearching() {
        composeTestRule.setContent {
            ExploreScreen(
                uiState = ExploreUiState(catalog = listOf(podcast("p1", "Kotlin Weekly"))),
                onQueryChange = {},
                onPodcastSelected = {},
            )
        }

        composeTestRule.onNodeWithText("Kotlin Weekly").assertExists()
    }

    @Test
    fun noResults_showsNoResultsState() {
        composeTestRule.setContent {
            ExploreScreen(
                uiState = ExploreUiState(query = "xyz", searchResults = emptyList()),
                onQueryChange = {},
                onPodcastSelected = {},
            )
        }

        composeTestRule.onNodeWithText("No results", substring = true).assertExists()
    }
}
