package com.desponsor.feature.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.desponsor.core.designsystem.PodcastArtwork
import com.desponsor.core.designsystem.TransportControls
import com.desponsor.core.model.PlaybackState
import kotlin.math.roundToInt

/**
 * The Player screen: artwork, title, timeline, and full transport controls
 * (FR-004–FR-006, FR-021). [scrubPositionSeconds] is non-null while the user
 * is dragging the timeline; it overrides [PlaybackState.positionSeconds] for
 * display until [onScrubEnd] commits the seek (FR-021).
 */
@Composable
fun PlayerScreen(
    state: PlaybackState,
    scrubPositionSeconds: Int?,
    onPlayPause: () -> Unit,
    onSkipForward30: () -> Unit,
    onSkipBackward30: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onScrubDrag: (Int) -> Unit,
    onScrubEnd: () -> Unit,
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
        val durationSeconds = episode?.durationSeconds?.coerceAtLeast(1) ?: 1
        // While not scrubbing, use the sub-second positionMillis (not the
        // whole-second positionSeconds) so the timeline visibly moves
        // between second boundaries too (FR-005 / SC-009).
        val sliderValue = scrubPositionSeconds?.toFloat() ?: (state.positionMillis / 1_000f)
        Slider(
            value = sliderValue,
            valueRange = 0f..durationSeconds.toFloat(),
            onValueChange = { newValue -> onScrubDrag(newValue.roundToInt().coerceIn(0, durationSeconds)) },
            onValueChangeFinished = onScrubEnd,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp).testTag("PlayerTimeline"),
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
