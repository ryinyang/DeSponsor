package com.desponsor.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desponsor.core.model.Episode
import com.desponsor.core.model.PlaybackState
import com.desponsor.core.model.clampPosition
import com.desponsor.core.player.PlaybackController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Thin wrapper around the session-scoped [PlaybackController] for the
 * Player screen (FR-004–FR-008, FR-021). Every transport control here
 * delegates straight to the matching controller method.
 */
class PlayerViewModel(
    private val playbackController: PlaybackController,
) : ViewModel() {
    val state: StateFlow<PlaybackState> = playbackController.observeState()

    private val _scrubPositionSeconds = MutableStateFlow<Int?>(null)

    /** Non-null while the user is dragging the timeline (FR-021). */
    val scrubPositionSeconds: StateFlow<Int?> = _scrubPositionSeconds.asStateFlow()

    fun loadEpisode(
        episode: Episode,
        podcastTitle: String,
        queue: List<Episode>,
    ) {
        viewModelScope.launch { playbackController.loadEpisode(episode, podcastTitle, queue) }
    }

    fun onPlayPause(isCurrentlyPlaying: Boolean) {
        if (isCurrentlyPlaying) playbackController.pause() else playbackController.play()
    }

    fun onSkipForward30() = playbackController.skipForward30()

    fun onSkipBackward30() = playbackController.skipBackward30()

    fun onNext() = playbackController.next()

    fun onPrevious() = playbackController.previous()

    /** Called live as the user drags the timeline; updates the display only. */
    fun onScrubDrag(positionSeconds: Int) {
        val duration = state.value.currentEpisode?.durationSeconds ?: return
        _scrubPositionSeconds.value = clampPosition(positionSeconds, duration)
    }

    /** Called on drag release; commits the scrubbed position as an actual seek. */
    fun onScrubEnd() {
        val target = _scrubPositionSeconds.value ?: return
        playbackController.seekTo(target)
        _scrubPositionSeconds.value = null
    }
}
