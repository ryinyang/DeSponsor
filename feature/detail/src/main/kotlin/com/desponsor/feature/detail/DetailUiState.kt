package com.desponsor.feature.detail

import com.desponsor.core.model.PodcastDetail

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Content(
        val detail: PodcastDetail,
    ) : DetailUiState
}
