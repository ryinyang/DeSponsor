package com.desponsor.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.desponsor.core.model.AppSettings
import com.desponsor.core.model.AppearanceTheme

/** Settings: appearance, default skip duration, default playback speed (FR-014). */
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onAppearanceThemeChange: (AppearanceTheme) -> Unit,
    onDefaultSkipSecondsChange: (Int) -> Unit,
    onDefaultPlaybackSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        SettingsSection(title = "Appearance") {
            Row {
                AppearanceTheme.entries.forEach { theme ->
                    FilterChip(
                        selected = settings.appearanceTheme == theme,
                        onClick = { onAppearanceThemeChange(theme) },
                        label = { Text(theme.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.padding(end = 8.dp),
                    )
                }
            }
        }

        SettingsSection(title = "Default skip duration") {
            Text("${settings.defaultSkipSeconds} seconds")
            Slider(
                value = settings.defaultSkipSeconds.toFloat(),
                onValueChange = { onDefaultSkipSecondsChange(it.toInt()) },
                valueRange = 10f..60f,
                steps = 9,
            )
        }

        SettingsSection(title = "Default playback speed") {
            Text("${"%.2f".format(settings.defaultPlaybackSpeed)}x")
            Slider(
                value = settings.defaultPlaybackSpeed,
                onValueChange = onDefaultPlaybackSpeedChange,
                valueRange = 0.5f..2f,
                steps = 5,
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}
