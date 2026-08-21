package com.desponsor.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desponsor.core.data.PodcastRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Maps [PodcastRepository]'s subscribed-podcasts flow to Home's UI state
 * (FR-001, FR-002). [scope] defaults to [viewModelScope] but is injectable
 * for tests.
 */
class HomeViewModel(
    private val podcastRepository: PodcastRepository,
    scope: CoroutineScope? = null,
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        podcastRepository
            .observeSubscribedPodcasts()
            .map { podcasts -> if (podcasts.isEmpty()) HomeUiState.Empty else HomeUiState.Content(podcasts) }
            .stateIn(
                scope = scope ?: viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading,
            )
}
