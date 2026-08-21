package com.desponsor.core.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "podcasts")
data class PodcastEntity(
    @PrimaryKey val id: String,
    val title: String,
    val publisher: String,
    val description: String,
    val artworkUrl: String?,
)

@Entity(tableName = "episodes")
data class EpisodeEntity(
    @PrimaryKey val id: String,
    val podcastId: String,
    val title: String,
    val durationSeconds: Int,
    val orderIndex: Int,
    val artworkUrl: String?,
    val sampleAudioRef: String,
)

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey val podcastId: String,
    val subscribedAtEpochMillis: Long,
)
