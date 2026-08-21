package com.desponsor.core.data

import com.desponsor.core.data.local.EpisodeEntity
import com.desponsor.core.data.local.PodcastDao
import com.desponsor.core.data.local.PodcastEntity
import com.desponsor.core.data.local.SubscriptionDao
import com.desponsor.core.data.local.SubscriptionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory test double for [PodcastDao] — keeps repository tests JVM-only, no DB needed. */
class FakePodcastDao : PodcastDao {
    private val podcasts = MutableStateFlow<List<PodcastEntity>>(emptyList())
    private val episodesByPodcast = mutableMapOf<String, List<EpisodeEntity>>()

    override fun observeCatalog(): Flow<List<PodcastEntity>> = podcasts

    override fun search(term: String): Flow<List<PodcastEntity>> =
        podcasts.map { list ->
            list.filter { it.title.contains(term, ignoreCase = true) || it.publisher.contains(term, ignoreCase = true) }
        }

    override suspend fun getPodcast(podcastId: String): PodcastEntity? = podcasts.value.find { it.id == podcastId }

    override suspend fun getEpisodes(podcastId: String): List<EpisodeEntity> = episodesByPodcast[podcastId].orEmpty()

    override suspend fun getEpisode(episodeId: String): EpisodeEntity? = episodesByPodcast.values.flatten().find { it.id == episodeId }

    override suspend fun podcastCount(): Int = podcasts.value.size

    override suspend fun insertPodcasts(podcasts: List<PodcastEntity>) {
        this.podcasts.value = (this.podcasts.value.filterNot { old -> podcasts.any { it.id == old.id } } + podcasts)
    }

    override suspend fun insertEpisodes(episodes: List<EpisodeEntity>) {
        episodes.groupBy { it.podcastId }.forEach { (podcastId, eps) -> episodesByPodcast[podcastId] = eps }
    }
}

/** In-memory test double for [SubscriptionDao]. */
class FakeSubscriptionDao : SubscriptionDao {
    private val subscribedIds = MutableStateFlow<List<String>>(emptyList())

    override suspend fun insert(subscription: SubscriptionEntity) {
        if (subscription.podcastId !in subscribedIds.value) {
            subscribedIds.value = subscribedIds.value + subscription.podcastId
        }
    }

    override suspend fun delete(podcastId: String) {
        subscribedIds.value = subscribedIds.value - podcastId
    }

    override fun observeSubscribedIds(): Flow<List<String>> = subscribedIds

    override suspend fun isSubscribed(podcastId: String): Boolean = podcastId in subscribedIds.value
}
