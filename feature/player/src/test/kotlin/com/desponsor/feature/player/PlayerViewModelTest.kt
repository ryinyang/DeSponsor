package com.desponsor.feature.player

import com.desponsor.core.model.Episode
import com.desponsor.core.model.PlaybackState
import com.desponsor.core.player.PlaybackController
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {
    private fun episode(id: String) = Episode(id, "p1", "Title $id", 90, 0, null, "ref-$id")

    @Test
    fun `loadEpisode delegates to the PlaybackController`() =
        runTest(UnconfinedTestDispatcher()) {
            val controller = mockk<PlaybackController>(relaxed = true)
            val state = MutableStateFlow(PlaybackState())
            every { controller.observeState() } returns state
            val viewModel = PlayerViewModel(controller)
            val episode = episode("e1")

            viewModel.loadEpisode(episode, "Podcast", listOf(episode))

            coVerify { controller.loadEpisode(episode, "Podcast", listOf(episode)) }
        }

    @Test
    fun `each transport control delegates to the matching PlaybackController method`() =
        runTest(UnconfinedTestDispatcher()) {
            val controller = mockk<PlaybackController>(relaxed = true)
            every { controller.observeState() } returns MutableStateFlow(PlaybackState())
            val viewModel = PlayerViewModel(controller)

            viewModel.onPlayPause(isCurrentlyPlaying = false)
            verify { controller.play() }

            viewModel.onPlayPause(isCurrentlyPlaying = true)
            verify { controller.pause() }

            viewModel.onSkipForward30()
            verify { controller.skipForward30() }

            viewModel.onSkipBackward30()
            verify { controller.skipBackward30() }

            viewModel.onNext()
            verify { controller.next() }

            viewModel.onPrevious()
            verify { controller.previous() }
        }
}
