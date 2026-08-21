package com.desponsor.core.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ValidationTest {
    private fun podcast(
        title: String = "Title",
        publisher: String = "Publisher",
    ) = Podcast(
        id = "p1",
        title = title,
        publisher = publisher,
        description = "desc",
        artworkUrl = null,
        isSubscribed = false,
    )

    @Test
    fun `blank title is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            requireValidPodcast(podcast(title = "  "))
        }
    }

    @Test
    fun `blank publisher is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            requireValidPodcast(podcast(publisher = ""))
        }
    }

    @Test
    fun `valid podcast passes`() {
        requireValidPodcast(podcast())
    }

    @Test
    fun `episode order index must be unique per podcast`() {
        val episodes =
            listOf(
                Episode("e1", "p1", "One", 60, 0, null, "sample1"),
                Episode("e2", "p1", "Two", 60, 0, null, "sample2"),
            )
        assertThrows(IllegalArgumentException::class.java) {
            requireUniqueOrderIndices(episodes)
        }
    }

    @Test
    fun `clampPosition never exceeds episode end`() {
        assertEquals(90, clampPosition(position = 200, durationSeconds = 90))
    }

    @Test
    fun `clampPosition never goes below zero`() {
        assertEquals(0, clampPosition(position = -30, durationSeconds = 90))
    }
}
