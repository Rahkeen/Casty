package dev.supergooey.casty.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.supergooey.casty.data.podcasts.Podcast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface PodcastDao {
  @Query("SELECT * FROM podcast")
  fun getAllPodcasts(): Flow<LocalPodcastWithEpisodes?>

  @Query("SELECT * FROM podcast WHERE name IS :name")
  fun getPodcastWithEpisodes(name: String): Flow<LocalPodcastWithEpisodes?>

  @Upsert
  fun upsert(podcast: LocalPodcast)
}

@Dao
interface EpisodeDao {
  @Upsert
  fun upsertAll(vararg episodes: LocalEpisode)
}