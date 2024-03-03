package dev.supergooey.casty.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import dev.supergooey.casty.data.podcasts.Podcast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface PodcastDao {
  @Transaction
  @Query("SELECT * FROM podcast")
  fun getAllPodcasts(): Flow<LocalPodcastWithEpisodes?>

  @Transaction
  @Query("SELECT * FROM podcast WHERE name IS :name")
  fun getPodcastWithEpisodes(name: String): Flow<LocalPodcastWithEpisodes?>

  @Upsert
  fun upsert(podcast: LocalPodcast)
}

@Dao
interface EpisodeDao {
  @Query("SELECT * FROM episode WHERE id IS :id")
  suspend fun getEpisode(id: String): LocalEpisode

  @Upsert
  fun upsertAll(vararg episodes: LocalEpisode)
}