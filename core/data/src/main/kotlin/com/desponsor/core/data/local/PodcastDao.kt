package com.desponsor.core.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PodcastDao {
    @Query("SELECT * FROM podcasts ORDER BY title")
    fun observeCatalog(): Flow<List<PodcastEntity>>

    @Query("SELECT * FROM podcasts WHERE title LIKE '%' || :term || '%' OR publisher LIKE '%' || :term || '%' ORDER BY title")
    fun search(term: String): Flow<List<PodcastEntity>>

    @Query("SELECT * FROM podcasts WHERE id = :podcastId")
    suspend fun getPodcast(podcastId: String): PodcastEntity?

    @Query("SELECT * FROM episodes WHERE podcastId = :podcastId ORDER BY orderIndex")
    suspend fun getEpisodes(podcastId: String): List<EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE id = :episodeId")
    suspend fun getEpisode(episodeId: String): EpisodeEntity?

    @Query("SELECT COUNT(*) FROM podcasts")
    suspend fun podcastCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPodcasts(podcasts: List<PodcastEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisodes(episodes: List<EpisodeEntity>)
}

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(subscription: SubscriptionEntity)

    @Query("DELETE FROM subscriptions WHERE podcastId = :podcastId")
    suspend fun delete(podcastId: String)

    @Query("SELECT podcastId FROM subscriptions")
    fun observeSubscribedIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM subscriptions WHERE podcastId = :podcastId)")
    suspend fun isSubscribed(podcastId: String): Boolean
}
