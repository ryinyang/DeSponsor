package com.desponsor.feature.explore

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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.desponsor.core.designsystem.EmptyState
import com.desponsor.core.designsystem.PodcastArtwork
import com.desponsor.core.model.Podcast

/** Explore + Search: a mocked discovery catalog with a search bar (FR-009, FR-010). */
@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    onQueryChange: (String) -> Unit,
    onPodcastSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            placeholder = { Text("Search podcasts") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )

        val results = uiState.searchResults
        when {
            results != null && results.isEmpty() ->
                EmptyState(
                    title = "No results",
                    message = "No podcasts match \"${uiState.query}\". Try a different search.",
                    icon = Icons.Filled.SearchOff,
                )
            else -> {
                val list = results ?: uiState.catalog
                LazyColumn {
                    items(list, key = { it.id }) { podcast ->
                        ExplorePodcastRow(podcast, onClick = { onPodcastSelected(podcast.id) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun ExplorePodcastRow(
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
        PodcastArtwork(artworkUrl = podcast.artworkUrl, contentDescription = podcast.title, modifier = Modifier.size(56.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = podcast.title)
            Text(text = podcast.publisher)
        }
    }
}
