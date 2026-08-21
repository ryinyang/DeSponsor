package com.desponsor.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * The app's primary screens (spec.md SC-001), as type-safe Navigation 3
 * routes. Search results are shown inline within Explore rather than as a
 * separate route, since the search bar lives on that same screen.
 */
@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object Home : AppRoute

    @Serializable
    data class Player(
        val episodeId: String,
    ) : AppRoute

    @Serializable
    data object Explore : AppRoute

    @Serializable
    data class Detail(
        val podcastId: String,
    ) : AppRoute

    @Serializable
    data object Settings : AppRoute
}
