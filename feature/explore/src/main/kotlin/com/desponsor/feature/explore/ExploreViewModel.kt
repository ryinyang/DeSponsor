package com.desponsor.feature.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desponsor.core.data.PodcastRepository
import com.desponsor.core.model.Podcast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/** Explore's mocked catalog plus search over it (FR-009, FR-010). */
@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModel(
    private val podcastRepository: PodcastRepository,
    scope: CoroutineScope? = null,
) : ViewModel() {
    private val query = MutableStateFlow("")

    private val searchResults =
        query.flatMapLatest { q ->
            if (q.isBlank()) flowOf<List<Podcast>?>(null) else podcastRepository.search(q)
        }

    val uiState: StateFlow<ExploreUiState> =
        combine(podcastRepository.observeExploreCatalog(), query, searchResults) { catalog, q, results ->
            ExploreUiState(catalog = catalog, query = q, searchResults = results)
        }.stateIn(
            scope = scope ?: viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExploreUiState(),
        )

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }
}
