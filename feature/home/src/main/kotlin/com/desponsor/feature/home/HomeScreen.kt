package com.desponsor.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.desponsor.core.designsystem.EmptyState
import com.desponsor.core.designsystem.PodcastArtwork
import com.desponsor.core.model.Podcast

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onPodcastSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        HomeUiState.Loading -> Unit
        HomeUiState.Empty ->
            EmptyState(
                title = "No podcasts yet",
                message = "Go to Explore to find and subscribe to a podcast.",
                icon = Icons.Filled.LibraryMusic,
                modifier = modifier,
            )
        is HomeUiState.Content ->
            LazyColumn(modifier = modifier) {
                items(uiState.podcasts, key = { it.id }) { podcast ->
                    PodcastRow(podcast = podcast, onClick = { onPodcastSelected(podcast.id) })
                    HorizontalDivider()
                }
            }
    }
}

@Composable
private fun PodcastRow(
    podcast: Podcast,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PodcastArtwork(
            artworkUrl = podcast.artworkUrl,
            contentDescription = podcast.title,
            modifier = Modifier.size(56.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = podcast.title)
            Text(text = podcast.publisher)
        }
    }
}
