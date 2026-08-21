package com.desponsor.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desponsor.core.data.PodcastRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Podcast Detail: loads a podcast's episode list and handles Subscribe (FR-011, FR-012, FR-013). */
class DetailViewModel(
    private val podcastRepository: PodcastRepository,
    private val podcastId: String,
    scope: CoroutineScope? = null,
) : ViewModel() {
    private val ownScope = scope ?: viewModelScope
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    private fun refresh() {
        ownScope.launch {
            podcastRepository.getPodcastDetail(podcastId)?.let { _uiState.value = DetailUiState.Content(it) }
        }
    }

    fun onSubscribeClick() {
        ownScope.launch {
            podcastRepository.subscribe(podcastId)
            refresh()
        }
    }
}
