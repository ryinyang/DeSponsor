package com.desponsor.app.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay30
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.desponsor.core.designsystem.PodcastArtwork
import com.desponsor.core.model.PlaybackState

/**
 * A persistent mini-player, visible on Home/Explore/Settings whenever an
 * episode is loaded, so playback state stays reachable while navigating
 * (FR-008). Tapping it opens the full Player.
 */
@Composable
fun MiniPlayerHost(
    state: PlaybackState,
    onOpenPlayer: () -> Unit,
    onPlayPause: () -> Unit,
    onSkipForward30: () -> Unit,
    onSkipBackward30: () -> Unit,
) {
    val episode = state.currentEpisode ?: return
    Surface(tonalElevation = 3.dp) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenPlayer)
                    .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PodcastArtwork(
                artworkUrl = episode.artworkUrl,
                contentDescription = episode.title,
                modifier = Modifier.size(40.dp),
            )
            Text(
                text = episode.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                maxLines = 1,
            )
            IconButton(onClick = onSkipBackward30) {
                Icon(Icons.Filled.Replay30, contentDescription = "Skip backward 30 seconds")
            }
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (state.isPlaying) "Pause" else "Play",
                )
            }
            IconButton(onClick = onSkipForward30) {
                Icon(Icons.Filled.Forward30, contentDescription = "Skip forward 30 seconds")
            }
        }
    }
}
