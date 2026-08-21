package com.desponsor.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * Shows a podcast/episode's artwork, or a clear placeholder when none is
 * available or the load fails (FR-003) — used on Home, Player, Explore,
 * and Podcast Detail.
 */
@Composable
fun PodcastArtwork(
    artworkUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val placeholder = rememberVectorPainter(Icons.Filled.Podcasts)
    AsyncImage(
        model = artworkUrl,
        contentDescription = contentDescription,
        placeholder = placeholder,
        error = placeholder,
        fallback = placeholder,
        contentScale = ContentScale.Crop,
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .fillMaxSize(),
    )
}
