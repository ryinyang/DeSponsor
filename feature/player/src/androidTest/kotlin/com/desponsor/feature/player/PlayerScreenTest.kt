package com.desponsor.feature.player

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.desponsor.core.model.Episode
import com.desponsor.core.model.PlaybackState
import org.junit.Rule
import org.junit.Test

class PlayerScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val episode = Episode("e1", "p1", "Episode Title", 90, 0, null, "ref")

    @Test
    fun showsEpisodeAndPodcastTitle() {
        composeTestRule.setContent {
            PlayerScreen(
                state = PlaybackState(currentEpisode = episode, podcastTitle = "My Podcast"),
                onPlayPause = {},
                onSkipForward30 = {},
                onSkipBackward30 = {},
                onNext = {},
                onPrevious = {},
            )
        }

        composeTestRule.onNodeWithText("Episode Title").assertExists()
        composeTestRule.onNodeWithText("My Podcast").assertExists()
    }

    @Test
    fun tappingPlay_invokesCallback() {
        var played = false
        composeTestRule.setContent {
            PlayerScreen(
                state = PlaybackState(currentEpisode = episode, podcastTitle = "My Podcast", isPlaying = false),
                onPlayPause = { played = true },
                onSkipForward30 = {},
                onSkipBackward30 = {},
                onNext = {},
                onPrevious = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("Play").performClick()
        assert(played)
    }
}
