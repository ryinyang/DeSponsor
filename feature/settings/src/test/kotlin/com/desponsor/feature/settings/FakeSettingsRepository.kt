package com.desponsor.feature.settings

import com.desponsor.core.data.SettingsRepository
import com.desponsor.core.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository : SettingsRepository {
    private val state = MutableStateFlow(AppSettings.Default)

    override fun observeSettings(): Flow<AppSettings> = state

    override suspend fun update(settings: AppSettings) {
        state.value = settings
    }
}
