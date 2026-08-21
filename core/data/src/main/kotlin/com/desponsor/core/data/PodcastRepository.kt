package com.desponsor.core.data

import com.desponsor.core.data.local.PodcastDao
import com.desponsor.core.data.local.SubscriptionDao
import com.desponsor.core.data.local.SubscriptionEntity
import com.desponsor.core.model.Episode
import com.desponsor.core.model.Podcast
import com.desponsor.core.model.PodcastDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/** See specs/001-podcast-app-mockup/contracts/module-interfaces.md. */
interface PodcastRepository {
    fun observeSubscribedPodcasts(): Flow<List<Podcast>>

    fun observeExploreCatalog(): Flow<List<Podcast>>

    fun search(query: String): Flow<List<Podcast>>

    suspend fun getPodcastDetail(podcastId: String): PodcastDetail?

    suspend fun subscribe(podcastId: String)

    suspend fun unsubscribe(podcastId: String)
}

class PodcastRepositoryImpl(
    private val podcastDao: PodcastDao,
    private val subscriptionDao: SubscriptionDao,
) : PodcastRepository {
    override fun observeSubscribedPodcasts(): Flow<List<Podcast>> =
        combine(podcastDao.observeCatalog(), subscriptionDao.observeSubscribedIds()) { catalog, subscribedIds ->
            catalog.filter { it.id in subscribedIds }.map { it.toModel(isSubscribed = true) }
        }

    override fun observeExploreCatalog(): Flow<List<Podcast>> = withSubscriptionFlags(podcastDao.observeCatalog())

    override fun search(query: String): Flow<List<Podcast>> = withSubscriptionFlags(podcastDao.search(query))

    override suspend fun getPodcastDetail(podcastId: String): PodcastDetail? {
        val podcast = podcastDao.getPodcast(podcastId) ?: return null
        val isSubscribed = subscriptionDao.isSubscribed(podcastId)
        val episodes = podcastDao.getEpisodes(podcastId).map { it.toModel() }
        return PodcastDetail(podcast.toModel(isSubscribed), episodes)
    }

    override suspend fun subscribe(podcastId: String) {
        subscriptionDao.insert(SubscriptionEntity(podcastId, System.currentTimeMillis()))
    }

    override suspend fun unsubscribe(podcastId: String) {
        subscriptionDao.delete(podcastId)
    }

    private fun withSubscriptionFlags(entities: Flow<List<com.desponsor.core.data.local.PodcastEntity>>): Flow<List<Podcast>> =
        combine(entities, subscriptionDao.observeSubscribedIds()) { list, subscribedIds ->
            list.map { it.toModel(isSubscribed = it.id in subscribedIds) }
        }
}

internal fun com.desponsor.core.data.local.PodcastEntity.toModel(isSubscribed: Boolean) =
    Podcast(
        id = id,
        title = title,
        publisher = publisher,
        description = description,
        artworkUrl = artworkUrl,
        isSubscribed = isSubscribed,
    )

internal fun com.desponsor.core.data.local.EpisodeEntity.toModel() =
    Episode(
        id = id,
        podcastId = podcastId,
        title = title,
        durationSeconds = durationSeconds,
        orderIndex = orderIndex,
        artworkUrl = artworkUrl,
        sampleAudioRef = sampleAudioRef,
    )
