package com.desponsor.core.model

enum class AppearanceTheme { SYSTEM, LIGHT, DARK }

/** App-wide preferences shown on the Settings screen. */
data class AppSettings(
    val appearanceTheme: AppearanceTheme = AppearanceTheme.SYSTEM,
    val defaultSkipSeconds: Int = 30,
    val defaultPlaybackSpeed: Float = 1.0f,
) {
    companion object {
        val Default = AppSettings()
    }
}
