package com.desponsor.feature.explore

import app.cash.turbine.test
import com.desponsor.core.model.Podcast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {
    private fun podcast(
        id: String,
        title: String,
    ) = Podcast(id, title, "Publisher", "desc", null, isSubscribed = false)

    @Test
    fun `catalog is shown before any search is typed`() =
        runTest(UnconfinedTestDispatcher()) {
            val catalog = MutableStateFlow(listOf(podcast("p1", "Kotlin Weekly")))
            val viewModel = ExploreViewModel(FakePodcastRepository(catalog), backgroundScope)

            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals(1, state.catalog.size)
                assertNull(state.searchResults)
            }
        }

    @Test
    fun `searching with no match produces an empty results list`() =
        runTest(UnconfinedTestDispatcher()) {
            val catalog = MutableStateFlow(listOf(podcast("p1", "Kotlin Weekly")))
            val viewModel = ExploreViewModel(FakePodcastRepository(catalog), backgroundScope)

            viewModel.onQueryChange("nonsense-xyz")

            viewModel.uiState.test {
                assertTrue(awaitItem().searchResults?.isEmpty() == true)
            }
        }

    @Test
    fun `searching matches catalog entries`() =
        runTest(UnconfinedTestDispatcher()) {
            val catalog = MutableStateFlow(listOf(podcast("p1", "Kotlin Weekly")))
            val viewModel = ExploreViewModel(FakePodcastRepository(catalog), backgroundScope)

            viewModel.onQueryChange("kotlin")

            viewModel.uiState.test {
                assertEquals(1, awaitItem().searchResults?.size)
            }
        }
}
