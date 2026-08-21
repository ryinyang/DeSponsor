package com.desponsor.feature.detail

import app.cash.turbine.test
import com.desponsor.core.model.Podcast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    private val podcast = Podcast("p1", "Kotlin Weekly", "Publisher", "desc", null, isSubscribed = false)

    @Test
    fun `loads the podcast detail on start`() =
        runTest(UnconfinedTestDispatcher()) {
            val repository = FakePodcastRepository(podcast)
            val viewModel = DetailViewModel(repository, "p1", backgroundScope)

            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state is DetailUiState.Content)
                assertFalse((state as DetailUiState.Content).detail.podcast.isSubscribed)
            }
        }

    @Test
    fun `subscribing reflects a Subscribed state, even if tapped twice`() =
        runTest(UnconfinedTestDispatcher()) {
            val repository = FakePodcastRepository(podcast)
            val viewModel = DetailViewModel(repository, "p1", backgroundScope)

            viewModel.onSubscribeClick()
            viewModel.onSubscribeClick()

            viewModel.uiState.test {
                val state = awaitItem() as DetailUiState.Content
                assertTrue(state.detail.podcast.isSubscribed)
            }
        }
}
