package com.desponsor.feature.detail

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.desponsor.core.model.Episode
import com.desponsor.core.model.Podcast
import com.desponsor.core.model.PodcastDetail
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val podcast = Podcast("p1", "Kotlin Weekly", "Publisher", "A show about Kotlin.", null, isSubscribed = false)
    private val episode = Episode("e1", "p1", "Episode One", 90, 0, null, "ref")

    @Test
    fun showsPodcastInfoAndEpisodes() {
        composeTestRule.setContent {
            DetailScreen(
                uiState = DetailUiState.Content(PodcastDetail(podcast, listOf(episode))),
                onSubscribeClick = {},
            )
        }

        composeTestRule.onNodeWithText("Kotlin Weekly").assertExists()
        composeTestRule.onNodeWithText("A show about Kotlin.").assertExists()
        composeTestRule.onNodeWithText("Episode One").assertExists()
        composeTestRule.onNodeWithText("Subscribe").assertExists()
    }

    @Test
    fun subscribedState_showsSubscribedLabel() {
        composeTestRule.setContent {
            DetailScreen(
                uiState = DetailUiState.Content(PodcastDetail(podcast.copy(isSubscribed = true), listOf(episode))),
                onSubscribeClick = {},
            )
        }

        composeTestRule.onNodeWithText("Subscribed").assertExists()
    }
}
