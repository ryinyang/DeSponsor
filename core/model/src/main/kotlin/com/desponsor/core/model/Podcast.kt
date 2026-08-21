package com.desponsor.core.model

/** A show the user can subscribe to. */
data class Podcast(
    val id: String,
    val title: String,
    val publisher: String,
    val description: String,
    val artworkUrl: String?,
    val isSubscribed: Boolean,
)

/** A podcast plus its ordered episode list, for the Podcast Detail screen. */
data class PodcastDetail(
    val podcast: Podcast,
    val episodes: List<Episode>,
)
