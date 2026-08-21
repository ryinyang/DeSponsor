package com.desponsor.core.model

/** The link between the user and a podcast that puts it on Home. */
data class Subscription(
    val podcastId: String,
    val subscribedAtEpochMillis: Long,
)
