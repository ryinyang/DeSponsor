package com.desponsor.core.player

import com.desponsor.core.model.Episode
import com.desponsor.core.model.PlaybackState
import com.desponsor.core.model.clampPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** See specs/001-podcast-app-mockup/contracts/module-interfaces.md. */
interface PlaybackController {
    fun observeState(): StateFlow<PlaybackState>

    fun play()

    fun pause()

    fun skipForward30()

    fun skipBackward30()

    fun next()

    fun previous()

    /** Seeks to an arbitrary position, clamped to `[0, durationSeconds]` (FR-021). */
    fun seekTo(positionSeconds: Int)

    suspend fun loadEpisode(
        episode: Episode,
        podcastTitle: String,
        queue: List<Episode>,
    )
}

private const val SKIP_SECONDS = 30

class PlaybackControllerImpl(
    private val engine: AudioEngine,
    private val scope: CoroutineScope,
) : PlaybackController {
    private val state = MutableStateFlow(PlaybackState())
    private var queue: List<Episode> = emptyList()

    init {
        // Keeps position visually live while playing (FR-005), without the
        // caller having to poll — a plain progress-tick loop is simpler
        // than plumbing ExoPlayer listener callbacks through AudioEngine.
        // Ticks every 250ms and reads positionMillis (not just the
        // whole-second positionSeconds) so the timeline actually appears
        // smooth (SC-009) rather than only updating once a whole second of
        // truncation has passed.
        scope.launch {
            while (true) {
                delay(250)
                if (state.value.isPlaying) {
                    state.value =
                        state.value.copy(
                            positionSeconds = engine.currentPositionSeconds(),
                            positionMillis = engine.currentPositionMillis(),
                        )
                }
            }
        }
    }

    override fun observeState(): StateFlow<PlaybackState> = state.asStateFlow()

    override suspend fun loadEpisode(
        episode: Episode,
        podcastTitle: String,
        queue: List<Episode>,
    ) {
        this.queue = queue.sortedBy { it.orderIndex }
        engine.setSource(episode.sampleAudioRef)
        engine.seekTo(0)
        state.value =
            PlaybackState(
                currentEpisode = episode,
                podcastTitle = podcastTitle,
                isPlaying = false,
                positionSeconds = 0,
                positionMillis = 0,
            )
    }

    override fun play() {
        engine.play()
        state.value = state.value.copy(isPlaying = true)
    }

    override fun pause() {
        engine.pause()
        state.value =
            state.value.copy(
                isPlaying = false,
                positionSeconds = engine.currentPositionSeconds(),
                positionMillis = engine.currentPositionMillis(),
            )
    }

    override fun skipForward30() = seekBy(SKIP_SECONDS)

    override fun skipBackward30() = seekBy(-SKIP_SECONDS)

    override fun seekTo(positionSeconds: Int) {
        val episode = state.value.currentEpisode ?: return
        val newPosition = clampPosition(positionSeconds, episode.durationSeconds)
        engine.seekTo(newPosition)
        state.value = state.value.copy(positionSeconds = newPosition, positionMillis = newPosition * 1_000L)
    }

    override fun next() = jumpTo(offset = 1)

    override fun previous() = jumpTo(offset = -1)

    private fun seekBy(deltaSeconds: Int) {
        val episode = state.value.currentEpisode ?: return
        val newPosition = clampPosition(state.value.positionSeconds + deltaSeconds, episode.durationSeconds)
        engine.seekTo(newPosition)
        state.value = state.value.copy(positionSeconds = newPosition, positionMillis = newPosition * 1_000L)
    }

    private fun jumpTo(offset: Int) {
        val current = state.value.currentEpisode ?: return
        val podcastTitle = state.value.podcastTitle ?: return
        val index = queue.indexOfFirst { it.id == current.id }
        val target = queue.getOrNull(index + offset) ?: return
        scope.launch { loadEpisode(target, podcastTitle, queue) }
    }
}
