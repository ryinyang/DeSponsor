package com.desponsor.feature.explore

import com.desponsor.core.model.Podcast

/**
 * `searchResults == null` means the user hasn't typed a search term yet
 * (show [catalog]); an empty list means a search ran and matched nothing
 * (FR-010's "no results" state).
 */
data class ExploreUiState(
    val catalog: List<Podcast> = emptyList(),
    val query: String = "",
    val searchResults: List<Podcast>? = null,
)
