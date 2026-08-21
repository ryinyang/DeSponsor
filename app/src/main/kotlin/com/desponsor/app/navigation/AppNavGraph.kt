package com.desponsor.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.desponsor.app.AppContainer
import com.desponsor.feature.detail.DetailScreen
import com.desponsor.feature.detail.DetailViewModel
import com.desponsor.feature.explore.ExploreScreen
import com.desponsor.feature.explore.ExploreViewModel
import com.desponsor.feature.home.HomeScreen
import com.desponsor.feature.home.HomeViewModel
import com.desponsor.feature.player.PlayerScreen
import com.desponsor.feature.player.PlayerViewModel
import com.desponsor.feature.settings.SettingsScreen
import com.desponsor.feature.settings.SettingsViewModel
import kotlinx.coroutines.launch

/**
 * The app's Navigation 3 graph (T020, T030, T040, T045). [container]'s
 * `playbackController` is a session-scoped holder created above this
 * graph, so playback state survives navigating between Home, Explore, and
 * Settings (FR-008).
 */
@Composable
fun AppNavGraph(container: AppContainer) {
    val backStack = rememberNavBackStack(AppRoute.Home)
    val scope = rememberCoroutineScope()

    val homeViewModel = remember { HomeViewModel(container.podcastRepository) }
    val playerViewModel = remember { PlayerViewModel(container.playbackController) }
    val exploreViewModel = remember { ExploreViewModel(container.podcastRepository) }
    val settingsViewModel = remember { SettingsViewModel(container.settingsRepository) }
    val playbackState by playerViewModel.state.collectAsStateWithLifecycle()

    fun openPodcastsFirstEpisode(podcastId: String) {
        scope.launch {
            val detail = container.podcastRepository.getPodcastDetail(podcastId) ?: return@launch
            val firstEpisode = detail.episodes.firstOrNull() ?: return@launch
            playerViewModel.loadEpisode(firstEpisode, detail.podcast.title, detail.episodes)
            backStack.add(AppRoute.Player(firstEpisode.id))
        }
    }

    val currentRoute = backStack.lastOrNull() as? AppRoute

    Scaffold(
        bottomBar = {
            DeSponsorBottomBar(
                current = currentRoute,
                onSelect = { route ->
                    backStack.clear()
                    backStack.add(route)
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Box(modifier = Modifier.weight(1f)) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                    entryProvider =
                        entryProvider {
                            entry<AppRoute.Home> {
                                val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
                                HomeScreen(uiState = uiState, onPodcastSelected = ::openPodcastsFirstEpisode)
                            }
                            entry<AppRoute.Player> {
                                val scrubPositionSeconds by playerViewModel.scrubPositionSeconds.collectAsStateWithLifecycle()
                                PlayerScreen(
                                    state = playbackState,
                                    scrubPositionSeconds = scrubPositionSeconds,
                                    onPlayPause = { playerViewModel.onPlayPause(playbackState.isPlaying) },
                                    onSkipForward30 = playerViewModel::onSkipForward30,
                                    onSkipBackward30 = playerViewModel::onSkipBackward30,
                                    onNext = playerViewModel::onNext,
                                    onPrevious = playerViewModel::onPrevious,
                                    onScrubDrag = playerViewModel::onScrubDrag,
                                    onScrubEnd = playerViewModel::onScrubEnd,
                                )
                            }
                            entry<AppRoute.Explore> {
                                val uiState by exploreViewModel.uiState.collectAsStateWithLifecycle()
                                ExploreScreen(
                                    uiState = uiState,
                                    onQueryChange = exploreViewModel::onQueryChange,
                                    onPodcastSelected = { podcastId -> backStack.add(AppRoute.Detail(podcastId)) },
                                )
                            }
                            entry<AppRoute.Detail> { key ->
                                val detailViewModel =
                                    remember(key.podcastId) {
                                        DetailViewModel(container.podcastRepository, key.podcastId)
                                    }
                                val uiState by detailViewModel.uiState.collectAsStateWithLifecycle()
                                DetailScreen(uiState = uiState, onSubscribeClick = detailViewModel::onSubscribeClick)
                            }
                            entry<AppRoute.Settings> {
                                val settings by settingsViewModel.uiState.collectAsStateWithLifecycle()
                                SettingsScreen(
                                    settings = settings,
                                    onAppearanceThemeChange = settingsViewModel::onAppearanceThemeChange,
                                    onDefaultSkipSecondsChange = settingsViewModel::onDefaultSkipSecondsChange,
                                    onDefaultPlaybackSpeedChange = settingsViewModel::onDefaultPlaybackSpeedChange,
                                )
                            }
                        },
                )
            }
            if (currentRoute !is AppRoute.Player) {
                MiniPlayerHost(
                    state = playbackState,
                    onOpenPlayer = { playbackState.currentEpisode?.let { backStack.add(AppRoute.Player(it.id)) } },
                    onPlayPause = { playerViewModel.onPlayPause(playbackState.isPlaying) },
                    onSkipForward30 = playerViewModel::onSkipForward30,
                    onSkipBackward30 = playerViewModel::onSkipBackward30,
                )
            }
        }
    }
}
