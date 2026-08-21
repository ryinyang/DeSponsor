package com.desponsor.feature.player

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
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
                scrubPositionSeconds = null,
                onPlayPause = {},
                onSkipForward30 = {},
                onSkipBackward30 = {},
                onNext = {},
                onPrevious = {},
                onScrubDrag = {},
                onScrubEnd = {},
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
                scrubPositionSeconds = null,
                onPlayPause = { played = true },
                onSkipForward30 = {},
                onSkipBackward30 = {},
                onNext = {},
                onPrevious = {},
                onScrubDrag = {},
                onScrubEnd = {},
            )
        }

        composeTestRule.onNodeWithContentDescription("Play").performClick()
        assert(played)
    }

    @Test
    fun draggingTimeline_updatesLiveAndCommitsOnRelease() {
        val dragPositions = mutableListOf<Int>()
        var ended = false
        composeTestRule.setContent {
            PlayerScreen(
                state = PlaybackState(currentEpisode = episode, podcastTitle = "My Podcast", positionSeconds = 10),
                scrubPositionSeconds = null,
                onPlayPause = {},
                onSkipForward30 = {},
                onSkipBackward30 = {},
                onNext = {},
                onPrevious = {},
                onScrubDrag = { dragPositions.add(it) },
                onScrubEnd = { ended = true },
            )
        }

        composeTestRule.onNodeWithTag("PlayerTimeline").performTouchInput { swipeRight() }

        assert(dragPositions.isNotEmpty()) { "expected the drag to report live position updates" }
        assert(ended) { "expected releasing the drag to commit the scrub" }
    }
}
