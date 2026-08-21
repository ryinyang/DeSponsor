package com.desponsor.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay30
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** The Player's full transport control row: previous, skip back 30s, play/pause, skip forward 30s, next (FR-004). */
@Composable
fun TransportControls(
    isPlaying: Boolean,
    onPrevious: () -> Unit,
    onSkipBackward30: () -> Unit,
    onPlayPause: () -> Unit,
    onSkipForward30: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous episode")
        }
        IconButton(onClick = onSkipBackward30) {
            Icon(Icons.Filled.Replay30, contentDescription = "Skip backward 30 seconds")
        }
        IconButton(onClick = onPlayPause) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
            )
        }
        IconButton(onClick = onSkipForward30) {
            Icon(Icons.Filled.Forward30, contentDescription = "Skip forward 30 seconds")
        }
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.SkipNext, contentDescription = "Next episode")
        }
    }
}
