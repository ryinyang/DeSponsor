package com.desponsor.feature.explore

import com.desponsor.core.data.PodcastRepository
import com.desponsor.core.model.Podcast
import com.desponsor.core.model.PodcastDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakePodcastRepository(
    private val catalog: MutableStateFlow<List<Podcast>> = MutableStateFlow(emptyList()),
) : PodcastRepository {
    override fun observeSubscribedPodcasts(): Flow<List<Podcast>> = MutableStateFlow(emptyList())

    override fun observeExploreCatalog(): Flow<List<Podcast>> = catalog

    override fun search(query: String): Flow<List<Podcast>> =
        catalog.map { list -> list.filter { it.title.contains(query, ignoreCase = true) } }

    override suspend fun getPodcastDetail(podcastId: String): PodcastDetail? = null

    override suspend fun subscribe(podcastId: String) = Unit

    override suspend fun unsubscribe(podcastId: String) = Unit
}
