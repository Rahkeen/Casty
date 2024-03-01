package dev.supergooey.casty.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [LocalPodcast::class, LocalEpisode::class],
  version = 1,
  exportSchema = false
)
abstract class CastyDatabase: RoomDatabase() {
  abstract fun podcastDao(): PodcastDao
  abstract fun episodeDao(): EpisodeDao
}