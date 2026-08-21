package com.desponsor.core.data

import app.cash.turbine.test
import com.desponsor.core.data.local.SettingsStore
import com.desponsor.core.model.AppSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryTest {
    private class FakeSettingsStore : SettingsStore {
        val state = MutableStateFlow(AppSettings.Default)

        override fun observe(): Flow<AppSettings> = state

        override suspend fun update(settings: AppSettings) {
            state.value = settings
        }
    }

    @Test
    fun `update is reflected by observeSettings right away`() =
        runTest {
            val store = FakeSettingsStore()
            val repository = SettingsRepositoryImpl(store)

            repository.observeSettings().test {
                assertEquals(AppSettings.Default, awaitItem())

                repository.update(AppSettings(defaultSkipSeconds = 15))
                assertEquals(15, awaitItem().defaultSkipSeconds)
            }
        }
}
