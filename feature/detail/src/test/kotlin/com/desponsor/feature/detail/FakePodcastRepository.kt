package com.desponsor.feature.detail

import com.desponsor.core.data.PodcastRepository
import com.desponsor.core.model.Episode
import com.desponsor.core.model.Podcast
import com.desponsor.core.model.PodcastDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePodcastRepository(
    private val podcast: Podcast,
    private val episodes: List<Episode> = emptyList(),
) : PodcastRepository {
    private val subscribedIds = mutableSetOf<String>()

    override fun observeSubscribedPodcasts(): Flow<List<Podcast>> = MutableStateFlow(emptyList())

    override fun observeExploreCatalog(): Flow<List<Podcast>> = MutableStateFlow(listOf(podcast))

    override fun search(query: String): Flow<List<Podcast>> = MutableStateFlow(emptyList())

    override suspend fun getPodcastDetail(podcastId: String): PodcastDetail? {
        if (podcastId != podcast.id) return null
        return PodcastDetail(podcast.copy(isSubscribed = podcastId in subscribedIds), episodes)
    }

    override suspend fun subscribe(podcastId: String) {
        subscribedIds += podcastId
    }

    override suspend fun unsubscribe(podcastId: String) {
        subscribedIds -= podcastId
    }
}
