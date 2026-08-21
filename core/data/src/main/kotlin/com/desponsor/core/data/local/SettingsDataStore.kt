package com.desponsor.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.desponsor.core.model.AppSettings
import com.desponsor.core.model.AppearanceTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsStore: androidx.datastore.core.DataStore<Preferences> by
    preferencesDataStore(name = "settings")

private object SettingsKeys {
    val APPEARANCE_THEME = stringPreferencesKey("appearance_theme")
    val DEFAULT_SKIP_SECONDS = intPreferencesKey("default_skip_seconds")
    val DEFAULT_PLAYBACK_SPEED = floatPreferencesKey("default_playback_speed")
}

/** Abstraction over "where settings live," so the repository layer is fake-testable on the JVM. */
interface SettingsStore {
    fun observe(): Flow<AppSettings>

    suspend fun update(settings: AppSettings)
}

class SettingsDataStore(
    private val context: Context,
) : SettingsStore {
    override fun observe(): Flow<AppSettings> =
        context.settingsStore.data.map { prefs ->
            AppSettings(
                appearanceTheme =
                    prefs[SettingsKeys.APPEARANCE_THEME]?.let { AppearanceTheme.valueOf(it) }
                        ?: AppSettings.Default.appearanceTheme,
                defaultSkipSeconds =
                    prefs[SettingsKeys.DEFAULT_SKIP_SECONDS] ?: AppSettings.Default.defaultSkipSeconds,
                defaultPlaybackSpeed =
                    prefs[SettingsKeys.DEFAULT_PLAYBACK_SPEED] ?: AppSettings.Default.defaultPlaybackSpeed,
            )
        }

    override suspend fun update(settings: AppSettings) {
        context.settingsStore.edit { prefs ->
            prefs[SettingsKeys.APPEARANCE_THEME] = settings.appearanceTheme.name
            prefs[SettingsKeys.DEFAULT_SKIP_SECONDS] = settings.defaultSkipSeconds
            prefs[SettingsKeys.DEFAULT_PLAYBACK_SPEED] = settings.defaultPlaybackSpeed
        }
    }
}
