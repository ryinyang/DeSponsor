package com.desponsor.core.data

import com.desponsor.core.data.local.SettingsStore
import com.desponsor.core.model.AppSettings
import kotlinx.coroutines.flow.Flow

/** See specs/001-podcast-app-mockup/contracts/module-interfaces.md. */
interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>

    suspend fun update(settings: AppSettings)
}

class SettingsRepositoryImpl(
    private val store: SettingsStore,
) : SettingsRepository {
    override fun observeSettings(): Flow<AppSettings> = store.observe()

    override suspend fun update(settings: AppSettings) = store.update(settings)
}
