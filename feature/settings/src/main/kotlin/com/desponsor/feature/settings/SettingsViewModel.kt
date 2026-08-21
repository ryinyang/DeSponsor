package com.desponsor.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desponsor.core.data.SettingsRepository
import com.desponsor.core.model.AppSettings
import com.desponsor.core.model.AppearanceTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Settings: view and change app preferences (FR-014). */
class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    scope: CoroutineScope? = null,
) : ViewModel() {
    private val ownScope = scope ?: viewModelScope

    val uiState: StateFlow<AppSettings> =
        settingsRepository.observeSettings().stateIn(
            scope = ownScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings.Default,
        )

    fun onAppearanceThemeChange(theme: AppearanceTheme) = update { it.copy(appearanceTheme = theme) }

    fun onDefaultSkipSecondsChange(seconds: Int) = update { it.copy(defaultSkipSeconds = seconds) }

    fun onDefaultPlaybackSpeedChange(speed: Float) = update { it.copy(defaultPlaybackSpeed = speed) }

    private fun update(transform: (AppSettings) -> AppSettings) {
        ownScope.launch { settingsRepository.update(transform(uiState.value)) }
    }
}
