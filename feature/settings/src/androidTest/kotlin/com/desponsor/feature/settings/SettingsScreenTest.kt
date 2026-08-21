package com.desponsor.feature.settings

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.desponsor.core.model.AppSettings
import com.desponsor.core.model.AppearanceTheme
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun listsAllPreferences() {
        composeTestRule.setContent {
            SettingsScreen(
                settings = AppSettings.Default,
                onAppearanceThemeChange = {},
                onDefaultSkipSecondsChange = {},
                onDefaultPlaybackSpeedChange = {},
            )
        }

        composeTestRule.onNodeWithText("Appearance", substring = true).assertExists()
        composeTestRule.onNodeWithText("skip", substring = true, ignoreCase = true).assertExists()
        composeTestRule.onNodeWithText("playback speed", substring = true, ignoreCase = true).assertExists()
    }

    @Test
    fun changingAppearance_invokesCallback() {
        var changedTo: AppearanceTheme? = null
        composeTestRule.setContent {
            SettingsScreen(
                settings = AppSettings.Default,
                onAppearanceThemeChange = { changedTo = it },
                onDefaultSkipSecondsChange = {},
                onDefaultPlaybackSpeedChange = {},
            )
        }

        composeTestRule.onNodeWithText("Dark").performClick()
        assert(changedTo == AppearanceTheme.DARK)
    }
}
