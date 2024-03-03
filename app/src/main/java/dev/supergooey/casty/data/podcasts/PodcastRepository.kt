package dev.supergooey.casty.data.podcasts

import com.prof18.rssparser.model.RssChannel
import com.squareup.anvil.annotations.ContributesBinding
import dev.supergooey.casty.data.db.EpisodeDao
import dev.supergooey.casty.data.db.LocalEpisode
import dev.supergooey.casty.data.db.LocalPodcast
import dev.supergooey.casty.data.db.LocalPodcastWithEpisodes
import dev.supergooey.casty.data.db.PodcastDao
import dev.supergooey.casty.data.rssclient.RssClient
import dev.supergooey.casty.di.AppScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface PodcastRepository {
  suspend fun getPodcasts(): List<Podcast>
  fun getPodcast(id: String): Flow<Podcast>
  suspend fun fetchPodcast(url: String): Podcast
  suspend fun selectEpisode(episodeId: String): Episode
}

@Singleton
@ContributesBinding(AppScope::class)
class RealPodcastRepository @Inject constructor(
  private val localPodcasts: PodcastDao,
  private val localEpisodes: EpisodeDao,
  private val rssClient: RssClient
): PodcastRepository {

  override suspend fun getPodcasts(): List<Podcast> {
    return localPodcasts.getAllPodcasts().map { it.toPodcast() }
  }

  override fun getPodcast(id: String): Flow<Podcast> {
    return localPodcasts
      .getPodcastWithEpisodes(id)
      .filterNotNull()
      .map { it.toPodcast() }
  }

  override suspend fun fetchPodcast(url: String): Podcast {
    return withContext(Dispatchers.IO) {
      val podcast = rssClient.getRssFeed(url).toPodcast()
      // insert or update database with podcast data?
      localPodcasts.upsert(podcast.toLocalPodcast())
      val episodes = podcast.episodes.map(Episode::toLocalEpisode).toTypedArray()
      localEpisodes.upsertAll(*episodes)
      // return podcast
      podcast
    }
  }

  override suspend fun selectEpisode(episodeId: String): Episode {
    return withContext(Dispatchers.IO) {
      localEpisodes.getEpisode(episodeId).toEpisode()
    }
  }
}

private fun RssChannel.toPodcast(): Podcast {
  return Podcast(
    name = title!!,
    imageUrl = image?.url ?: "",
    episodes = items.map { item ->
      Episode(
        id = item.guid!!,
        podcastId = title!!,
        title = item.title!!,
        albumArtUrl = image?.url ?: "",
        audioUrl = item.audio!!
      )
    }
  )
}

data class Podcast(
  val name: String,
  val imageUrl: String,
  val episodes: List<Episode> = emptyList()
)

fun Podcast.toLocalPodcast(): LocalPodcast {
  return LocalPodcast(
    name = name,
    albumArtUrl = imageUrl
  )
}

data class Episode(
  val id: String,
  val title: String,
  val audioUrl: String,
  val albumArtUrl: String,
  val podcastId: String
)

fun Episode.toLocalEpisode(): LocalEpisode {
  return LocalEpisode(
    id = id,
    podcastName = podcastId,
    title = title,
    imageUrl = albumArtUrl,
    audioUrl = audioUrl
  )
}

fun LocalEpisode.toEpisode(): Episode {
  return Episode(
    id = id,
    podcastId = podcastName,
    title = title,
    albumArtUrl = imageUrl,
    audioUrl = audioUrl
  )
}

fun LocalPodcastWithEpisodes.toPodcast(): Podcast {
  return Podcast(
    name = podcast.name,
    imageUrl = podcast.albumArtUrl,
    episodes = episodes.map(LocalEpisode::toEpisode)
  )
}
