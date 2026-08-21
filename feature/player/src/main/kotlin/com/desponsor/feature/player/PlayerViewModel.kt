package com.desponsor.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desponsor.core.model.Episode
import com.desponsor.core.model.PlaybackState
import com.desponsor.core.player.PlaybackController
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Thin wrapper around the session-scoped [PlaybackController] for the
 * Player screen (FR-004–FR-008). Every transport control here delegates
 * straight to the matching controller method.
 */
class PlayerViewModel(
    private val playbackController: PlaybackController,
) : ViewModel() {
    val state: StateFlow<PlaybackState> = playbackController.observeState()

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
}
