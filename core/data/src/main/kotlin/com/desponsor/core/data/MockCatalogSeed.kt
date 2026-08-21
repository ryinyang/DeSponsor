package com.desponsor.core.data

import com.desponsor.core.data.local.AppDatabase
import com.desponsor.core.data.local.EpisodeEntity
import com.desponsor.core.data.local.PodcastEntity
import com.desponsor.core.data.local.SubscriptionEntity

/**
 * Seeds a small mocked podcast/episode catalog on first run (FR-016).
 * Two podcasts start subscribed, so Home has content out of the box;
 * the rest are only visible via Explore/Search, per spec.md User Story 2.
 */
suspend fun seedIfEmpty(database: AppDatabase) {
    val podcastDao = database.podcastDao()
    if (podcastDao.podcastCount() > 0) return

    val podcasts =
        listOf(
            PodcastEntity("kotlin-weekly", "Kotlin Weekly", "JetBrains", "News from the Kotlin world.", null),
            PodcastEntity("android-dev", "Android Dev Digest", "DeSponsor Media", "Weekly Android engineering talk.", null),
            PodcastEntity("indie-makers", "Indie Makers", "Solo Founder Collective", "Stories from solo app builders.", null),
            PodcastEntity("history-hour", "History Hour", "Past Perfect Studios", "One story from history, every week.", null),
            PodcastEntity("science-now", "Science Now", "Bright Signal Audio", "Short explainers on current research.", null),
        )
    podcastDao.insertPodcasts(podcasts)

    val sampleRefs = listOf("sample_audio_1", "sample_audio_2", "sample_audio_3")
    val sampleDurations = mapOf("sample_audio_1" to 65, "sample_audio_2" to 95, "sample_audio_3" to 50)

    val episodes =
        podcasts.flatMap { podcast ->
            (0..2).map { index ->
                val ref = sampleRefs[index % sampleRefs.size]
                EpisodeEntity(
                    id = "${podcast.id}-ep$index",
                    podcastId = podcast.id,
                    title = "${podcast.title} — Episode ${index + 1}",
                    durationSeconds = sampleDurations.getValue(ref),
                    orderIndex = index,
                    artworkUrl = null,
                    sampleAudioRef = ref,
                )
            }
        }
    podcastDao.insertEpisodes(episodes)

    val subscriptionDao = database.subscriptionDao()
    val now = System.currentTimeMillis()
    subscriptionDao.insert(SubscriptionEntity("kotlin-weekly", now))
    subscriptionDao.insert(SubscriptionEntity("android-dev", now))
}
