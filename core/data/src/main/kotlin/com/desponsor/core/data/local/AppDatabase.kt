package com.desponsor.core.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(
    entities = [PodcastEntity::class, EpisodeEntity::class, SubscriptionEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao

    abstract fun subscriptionDao(): SubscriptionDao
}

fun createAppDatabase(context: Context): AppDatabase =
    Room
        .databaseBuilder(context.applicationContext, AppDatabase::class.java, "desponsor.db")
        .build()
