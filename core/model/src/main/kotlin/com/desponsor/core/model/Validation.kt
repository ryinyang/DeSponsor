package com.desponsor.core.model

fun requireValidPodcast(podcast: Podcast) {
    require(podcast.title.isNotBlank()) { "Podcast title must not be blank" }
    require(podcast.publisher.isNotBlank()) { "Podcast publisher must not be blank" }
}

fun requireUniqueOrderIndices(episodes: List<Episode>) {
    val duplicates = episodes.groupBy { it.orderIndex }.filterValues { it.size > 1 }
    require(duplicates.isEmpty()) { "Duplicate episode orderIndex within a podcast: ${duplicates.keys}" }
}

/** Clamps a playback position to the episode's valid range, per FR-004's skip edge cases. */
fun clampPosition(
    position: Int,
    durationSeconds: Int,
): Int = position.coerceIn(0, durationSeconds)
