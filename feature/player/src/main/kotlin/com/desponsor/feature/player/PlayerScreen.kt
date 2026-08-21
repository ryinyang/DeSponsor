package com.desponsor.feature.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.desponsor.core.designsystem.PodcastArtwork
import com.desponsor.core.designsystem.TransportControls
import com.desponsor.core.model.PlaybackState

/** The Player screen: artwork, title, progress, and full transport controls (FR-004–FR-006). */
@Composable
fun PlayerScreen(
    state: PlaybackState,
    onPlayPause: () -> Unit,
    onSkipForward30: () -> Unit,
    onSkipBackward30: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val episode = state.currentEpisode
    Column(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PodcastArtwork(
            artworkUrl = episode?.artworkUrl,
            contentDescription = episode?.title,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        )
        Text(
            text = episode?.title.orEmpty(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = state.podcastTitle.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        val progress = episode?.let { state.positionSeconds.toFloat() / it.durationSeconds.coerceAtLeast(1) } ?: 0f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        )
        TransportControls(
            isPlaying = state.isPlaying,
            onPrevious = onPrevious,
            onSkipBackward30 = onSkipBackward30,
            onPlayPause = onPlayPause,
            onSkipForward30 = onSkipForward30,
            onNext = onNext,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}
