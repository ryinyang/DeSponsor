package com.desponsor.feature.home

import com.desponsor.core.model.Podcast

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data object Empty : HomeUiState

    data class Content(
        val podcasts: List<Podcast>,
    ) : HomeUiState
}
