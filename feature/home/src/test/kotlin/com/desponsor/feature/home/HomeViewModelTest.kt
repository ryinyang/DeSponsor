package com.desponsor.feature.home

import app.cash.turbine.test
import com.desponsor.core.model.Podcast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private fun podcast(id: String) = Podcast(id, "Title $id", "Publisher", "desc", null, isSubscribed = true)

    @Test
    fun `empty subscriptions produce the Empty state`() =
        runTest(UnconfinedTestDispatcher()) {
            val subscribed = MutableStateFlow<List<Podcast>>(emptyList())
            val viewModel = HomeViewModel(FakePodcastRepository(subscribed), backgroundScope)

            viewModel.uiState.test {
                assertEquals(HomeUiState.Empty, awaitItem())
            }
        }

    @Test
    fun `subscribed podcasts produce the Content state`() =
        runTest(UnconfinedTestDispatcher()) {
            val subscribed = MutableStateFlow(listOf(podcast("p1"), podcast("p2")))
            val viewModel = HomeViewModel(FakePodcastRepository(subscribed), backgroundScope)

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state is HomeUiState.Content)
                assertEquals(2, (state as HomeUiState.Content).podcasts.size)
            }
        }
}
