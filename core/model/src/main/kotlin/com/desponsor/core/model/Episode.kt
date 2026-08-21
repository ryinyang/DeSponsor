package com.desponsor.core.model

/** A single playable item that belongs to a podcast. */
data class Episode(
    val id: String,
    val podcastId: String,
    val title: String,
    val durationSeconds: Int,
    val orderIndex: Int,
    val artworkUrl: String?,
    val sampleAudioRef: String,
)
