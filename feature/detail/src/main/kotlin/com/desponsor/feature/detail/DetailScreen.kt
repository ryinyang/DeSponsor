package com.desponsor.feature.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.desponsor.core.designsystem.PodcastArtwork
import com.desponsor.core.model.Episode
import com.desponsor.core.model.PodcastDetail

/** Podcast Detail: artwork, description, episode list, and Subscribe (FR-011, FR-012). */
@Composable
fun DetailScreen(
    uiState: DetailUiState,
    onSubscribeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        DetailUiState.Loading -> Unit
        is DetailUiState.Content -> DetailContent(uiState.detail, onSubscribeClick, modifier)
    }
}

@Composable
private fun DetailContent(
    detail: PodcastDetail,
    onSubscribeClick: () -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier) {
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                PodcastArtwork(
                    artworkUrl = detail.podcast.artworkUrl,
                    contentDescription = detail.podcast.title,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                )
                Text(
                    text = detail.podcast.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(text = detail.podcast.publisher, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = detail.podcast.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
                Button(
                    onClick = onSubscribeClick,
                    enabled = !detail.podcast.isSubscribed,
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Text(if (detail.podcast.isSubscribed) "Subscribed" else "Subscribe")
                }
            }
            HorizontalDivider()
        }
        items(detail.episodes, key = { it.id }) { episode ->
            EpisodeRow(episode)
            HorizontalDivider()
        }
    }
}

@Composable
private fun EpisodeRow(episode: Episode) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = episode.title, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "${episode.durationSeconds / 60} min",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
