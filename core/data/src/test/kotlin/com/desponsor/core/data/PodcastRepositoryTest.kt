package com.desponsor.core.data

import app.cash.turbine.test
import com.desponsor.core.data.local.PodcastEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PodcastRepositoryTest {
    private fun podcastEntity(
        id: String,
        title: String = "Title $id",
        publisher: String = "Publisher",
    ) = PodcastEntity(id = id, title = title, publisher = publisher, description = "desc", artworkUrl = null)

    private fun newRepository(): Pair<PodcastRepository, FakePodcastDao> {
        val podcastDao = FakePodcastDao()
        val subscriptionDao = FakeSubscriptionDao()
        return PodcastRepositoryImpl(podcastDao, subscriptionDao) to podcastDao
    }

    @Test
    fun `subscribing twice does not duplicate the subscription`() =
        runTest {
            val (repository, podcastDao) = newRepository()
            podcastDao.insertPodcasts(listOf(podcastEntity("p1")))

            repository.subscribe("p1")
            repository.subscribe("p1")

            repository.observeSubscribedPodcasts().test {
                assertEquals(1, awaitItem().size)
            }
        }

    @Test
    fun `observeSubscribedPodcasts reacts to subscribe and unsubscribe`() =
        runTest {
            val (repository, podcastDao) = newRepository()
            podcastDao.insertPodcasts(listOf(podcastEntity("p1")))

            repository.observeSubscribedPodcasts().test {
                assertEquals(0, awaitItem().size)

                repository.subscribe("p1")
                assertEquals(1, awaitItem().size)

                repository.unsubscribe("p1")
                assertEquals(0, awaitItem().size)
            }
        }

    @Test
    fun `search with no match returns an empty list`() =
        runTest {
            val (repository, podcastDao) = newRepository()
            podcastDao.insertPodcasts(listOf(podcastEntity("p1", title = "Kotlin Weekly")))

            repository.search("nonsense-term-xyz").test {
                assertTrue(awaitItem().isEmpty())
            }
        }

    @Test
    fun `search matches by title or publisher`() =
        runTest {
            val (repository, podcastDao) = newRepository()
            podcastDao.insertPodcasts(listOf(podcastEntity("p1", title = "Kotlin Weekly")))

            repository.search("kotlin").test {
                assertEquals(1, awaitItem().size)
            }
        }
}
