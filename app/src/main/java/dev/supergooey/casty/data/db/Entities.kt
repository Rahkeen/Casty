package dev.supergooey.casty.data.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "podcast")
data class LocalPodcast(
  @PrimaryKey val name: String,
  val albumArtUrl: String,
)

@Entity(tableName = "episode")
data class LocalEpisode(
  @PrimaryKey val id: String,
  val podcastName: String,
  val title: String,
  val audioUrl: String,
  val imageUrl: String,
)

data class LocalPodcastWithEpisodes(
  @Embedded
  val podcast: LocalPodcast,
  @Relation(
    parentColumn = "name",
    entityColumn = "podcastName"
  )
  val episodes: List<LocalEpisode>
)