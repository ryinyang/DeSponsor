package com.desponsor.feature.home

import com.desponsor.core.data.PodcastRepository
import com.desponsor.core.model.Podcast
import com.desponsor.core.model.PodcastDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePodcastRepository(
    private val subscribed: MutableStateFlow<List<Podcast>> = MutableStateFlow(emptyList()),
    private val catalog: MutableStateFlow<List<Podcast>> = MutableStateFlow(emptyList()),
) : PodcastRepository {
    override fun observeSubscribedPodcasts(): Flow<List<Podcast>> = subscribed

    override fun observeExploreCatalog(): Flow<List<Podcast>> = catalog

    override fun search(query: String): Flow<List<Podcast>> =
        MutableStateFlow(catalog.value.filter { it.title.contains(query, ignoreCase = true) })

    override suspend fun getPodcastDetail(podcastId: String): PodcastDetail? = null

    override suspend fun subscribe(podcastId: String) {
        val target = catalog.value.find { it.id == podcastId } ?: return
        if (subscribed.value.none { it.id == podcastId }) {
            subscribed.value = subscribed.value + target.copy(isSubscribed = true)
        }
    }

    override suspend fun unsubscribe(podcastId: String) {
        subscribed.value = subscribed.value.filterNot { it.id == podcastId }
    }
}
