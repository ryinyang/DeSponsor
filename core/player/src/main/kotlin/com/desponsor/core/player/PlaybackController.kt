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
        // Keeps positionSeconds visually live while playing (FR-005), without
        // the caller having to poll — a plain progress-tick loop is simpler
        // than plumbing ExoPlayer listener callbacks through AudioEngine.
        scope.launch {
            while (true) {
                delay(1_000)
                if (state.value.isPlaying) {
                    state.value = state.value.copy(positionSeconds = engine.currentPositionSeconds())
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
            )
    }

    override fun play() {
        engine.play()
        state.value = state.value.copy(isPlaying = true)
    }

    override fun pause() {
        engine.pause()
        state.value = state.value.copy(isPlaying = false, positionSeconds = engine.currentPositionSeconds())
    }

    override fun skipForward30() = seekBy(SKIP_SECONDS)

    override fun skipBackward30() = seekBy(-SKIP_SECONDS)

    override fun next() = jumpTo(offset = 1)

    override fun previous() = jumpTo(offset = -1)

    private fun seekBy(deltaSeconds: Int) {
        val episode = state.value.currentEpisode ?: return
        val newPosition = clampPosition(state.value.positionSeconds + deltaSeconds, episode.durationSeconds)
        engine.seekTo(newPosition)
        state.value = state.value.copy(positionSeconds = newPosition)
    }

    private fun jumpTo(offset: Int) {
        val current = state.value.currentEpisode ?: return
        val podcastTitle = state.value.podcastTitle ?: return
        val index = queue.indexOfFirst { it.id == current.id }
        val target = queue.getOrNull(index + offset) ?: return
        scope.launch { loadEpisode(target, podcastTitle, queue) }
    }
}
