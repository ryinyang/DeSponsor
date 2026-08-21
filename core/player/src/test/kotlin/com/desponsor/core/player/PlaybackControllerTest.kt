package com.desponsor.core.player

import com.desponsor.core.model.Episode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlaybackControllerTest {
    private class FakeAudioEngine : AudioEngine {
        var playing = false
        var positionMillis = 0L
        var lastSource: String? = null

        override fun setSource(sampleAudioRef: String) {
            lastSource = sampleAudioRef
            positionMillis = 0
        }

        override fun play() {
            playing = true
        }

        override fun pause() {
            playing = false
        }

        override fun seekTo(positionSeconds: Int) {
            positionMillis = positionSeconds * 1_000L
        }

        override fun currentPositionSeconds(): Int = (positionMillis / 1_000L).toInt()

        override fun currentPositionMillis(): Long = positionMillis
    }

    private fun episode(
        id: String,
        orderIndex: Int,
        durationSeconds: Int = 90,
    ) = Episode(id, "p1", "Title $id", durationSeconds, orderIndex, null, "ref-$id")

    @Test
    fun `play sets isPlaying true and pause sets it false`() =
        runTest(UnconfinedTestDispatcher()) {
            val engine = FakeAudioEngine()
            val controller = PlaybackControllerImpl(engine, backgroundScope)
            val queue = listOf(episode("e1", 0))
            controller.loadEpisode(queue[0], "Podcast", queue)

            controller.play()
            assertTrue(controller.observeState().value.isPlaying)

            controller.pause()
            assertFalse(controller.observeState().value.isPlaying)
        }

    @Test
    fun `skipForward30 clamps at the episode end`() =
        runTest(UnconfinedTestDispatcher()) {
            val engine = FakeAudioEngine()
            val controller = PlaybackControllerImpl(engine, backgroundScope)
            val episode = episode("e1", 0, durationSeconds = 40)
            controller.loadEpisode(episode, "Podcast", listOf(episode))

            controller.skipForward30()
            controller.skipForward30()

            assertEquals(40, controller.observeState().value.positionSeconds)
        }

    @Test
    fun `skipBackward30 clamps at zero`() =
        runTest(UnconfinedTestDispatcher()) {
            val engine = FakeAudioEngine()
            val controller = PlaybackControllerImpl(engine, backgroundScope)
            val episode = episode("e1", 0, durationSeconds = 90)
            controller.loadEpisode(episode, "Podcast", listOf(episode))

            controller.skipBackward30()

            assertEquals(0, controller.observeState().value.positionSeconds)
        }

    @Test
    fun `next is a no-op at the end of the queue`() =
        runTest(UnconfinedTestDispatcher()) {
            val engine = FakeAudioEngine()
            val controller = PlaybackControllerImpl(engine, backgroundScope)
            val queue = listOf(episode("e1", 0), episode("e2", 1))
            controller.loadEpisode(queue[1], "Podcast", queue)

            controller.next()

            assertEquals(
                "e2",
                controller
                    .observeState()
                    .value.currentEpisode
                    ?.id,
            )
        }

    @Test
    fun `previous is a no-op at the start of the queue`() =
        runTest(UnconfinedTestDispatcher()) {
            val engine = FakeAudioEngine()
            val controller = PlaybackControllerImpl(engine, backgroundScope)
            val queue = listOf(episode("e1", 0), episode("e2", 1))
            controller.loadEpisode(queue[0], "Podcast", queue)

            controller.previous()

            assertEquals(
                "e1",
                controller
                    .observeState()
                    .value.currentEpisode
                    ?.id,
            )
        }

    @Test
    fun `positionMillis ticks at least every 250ms while playing, independent of whole-second positionSeconds`() =
        runTest(UnconfinedTestDispatcher()) {
            val engine = FakeAudioEngine()
            val controller = PlaybackControllerImpl(engine, backgroundScope)
            val episode = episode("e1", 0, durationSeconds = 90)
            controller.loadEpisode(episode, "Podcast", listOf(episode))
            controller.play()

            // Simulate the engine having advanced by less than a whole
            // second — positionSeconds (Int) wouldn't change, but the
            // sub-second positionMillis the timeline renders from MUST
            // still reflect it within 250ms for the movement to actually
            // be visible (FR-005 / SC-009); polling more often is not
            // enough if the observed value has only whole-second
            // granularity.
            engine.positionMillis = 400
            advanceTimeBy(250)
            runCurrent()

            assertEquals(0, controller.observeState().value.positionSeconds)
            assertEquals(400L, controller.observeState().value.positionMillis)
        }

    @Test
    fun `seekTo commits an arbitrary position`() =
        runTest(UnconfinedTestDispatcher()) {
            val engine = FakeAudioEngine()
            val controller = PlaybackControllerImpl(engine, backgroundScope)
            val episode = episode("e1", 0, durationSeconds = 90)
            controller.loadEpisode(episode, "Podcast", listOf(episode))

            controller.seekTo(42)

            assertEquals(42, controller.observeState().value.positionSeconds)
            assertEquals(42_000L, engine.positionMillis)
        }

    @Test
    fun `seekTo clamps to the episode's start and end`() =
        runTest(UnconfinedTestDispatcher()) {
            val engine = FakeAudioEngine()
            val controller = PlaybackControllerImpl(engine, backgroundScope)
            val episode = episode("e1", 0, durationSeconds = 90)
            controller.loadEpisode(episode, "Podcast", listOf(episode))

            controller.seekTo(-10)
            assertEquals(0, controller.observeState().value.positionSeconds)

            controller.seekTo(1_000)
            assertEquals(90, controller.observeState().value.positionSeconds)
        }

    @Test
    fun `next loads the following episode in the queue`() =
        runTest(UnconfinedTestDispatcher()) {
            val engine = FakeAudioEngine()
            val controller = PlaybackControllerImpl(engine, backgroundScope)
            val queue = listOf(episode("e1", 0), episode("e2", 1))
            controller.loadEpisode(queue[0], "Podcast", queue)

            controller.next()

            assertEquals(
                "e2",
                controller
                    .observeState()
                    .value.currentEpisode
                    ?.id,
            )
            assertEquals("ref-e2", engine.lastSource)
        }
}
