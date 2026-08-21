package com.desponsor.core.model

/** A transient view over the mocked catalog for a given search term. */
data class SearchResult(
    val query: String,
    val results: List<Podcast>,
)
