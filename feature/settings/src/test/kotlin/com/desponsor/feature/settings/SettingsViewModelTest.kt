package com.desponsor.feature.settings

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @Test
    fun `changing default skip seconds is reflected right away`() =
        runTest(UnconfinedTestDispatcher()) {
            val repository = FakeSettingsRepository()
            val viewModel = SettingsViewModel(repository, backgroundScope)

            viewModel.onDefaultSkipSecondsChange(15)

            viewModel.uiState.test {
                assertEquals(15, awaitItem().defaultSkipSeconds)
            }
        }
}
