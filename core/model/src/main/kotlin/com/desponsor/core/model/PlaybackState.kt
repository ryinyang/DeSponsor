package com.desponsor.core.model

/**
 * The Player's current session state. Carries enough to render the Player
 * screen (title, podcast name, artwork) without core/player depending on
 * core/data — the caller that starts playback already has this data.
 */
data class PlaybackState(
    val currentEpisode: Episode? = null,
    val podcastTitle: String? = null,
    val isPlaying: Boolean = false,
    val positionSeconds: Int = 0,
    /** Sub-second position, used to render the timeline smoothly (FR-005 / SC-009). */
    val positionMillis: Long = 0,
) {
    companion object {
        val Idle = PlaybackState()
    }
}
