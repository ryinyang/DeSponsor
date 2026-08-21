package com.desponsor.app

import android.content.Context
import com.desponsor.core.data.PodcastRepository
import com.desponsor.core.data.PodcastRepositoryImpl
import com.desponsor.core.data.SettingsRepository
import com.desponsor.core.data.SettingsRepositoryImpl
import com.desponsor.core.data.local.SettingsDataStore
import com.desponsor.core.data.local.createAppDatabase
import com.desponsor.core.data.seedIfEmpty
import com.desponsor.core.player.Media3AudioEngine
import com.desponsor.core.player.PlaybackController
import com.desponsor.core.player.PlaybackControllerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Manual dependency container (research.md §8 — no DI framework yet).
 * Every module's public interface is what gets handed out here, so a
 * framework can be introduced later without feature modules changing.
 */
class AppContainer(
    context: Context,
) {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val database by lazy { createAppDatabase(context) }

    val podcastRepository: PodcastRepository by lazy {
        PodcastRepositoryImpl(database.podcastDao(), database.subscriptionDao())
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(SettingsDataStore(context))
    }

    val playbackController: PlaybackController by lazy {
        PlaybackControllerImpl(Media3AudioEngine(context), appScope)
    }

    init {
        appScope.launch { seedIfEmpty(database) }
    }
}
