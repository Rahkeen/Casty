package dev.supergooey.casty.podcasts

import com.prof18.rssparser.model.RssChannel
import dev.supergooey.casty.rssclient.RssClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface PodcastRepository {
  fun getPodcasts(): List<Podcast>
  fun getPodcast(id: String): Podcast
  suspend fun fetchPodcast(url: String): Podcast
  fun selectEpisode(podcastId: String, episodeId: String): Episode
}

class RealPodcastRepository(
  private val rssClient: RssClient
): PodcastRepository {

  override fun getPodcasts(): List<Podcast> {
    TODO()
  }

  override fun getPodcast(id: String): Podcast {
    TODO()
  }

  override suspend fun fetchPodcast(url: String): Podcast {
    return withContext(Dispatchers.IO) {
      val podcast = rssClient.getRssFeed(url).toPodcast()
      // insert or update database with podcast data?
      podcast
    }
  }

  override fun selectEpisode(podcastId: String, episodeId: String): Episode {
    TODO()
  }
}

private fun RssChannel.toPodcast(): Podcast {
  return Podcast(
    name = title!!,
    imageUrl = image?.url ?: "",
    episodes = items.map { item ->
      Episode(
        id = item.guid!!,
        title = item.title!!,
        audioUrl = item.audio!!
      )A
    }
  )
}

data class Podcast(
  val name: String,
  val imageUrl: String,
  val episodes: List<Episode> = emptyList()
)

data class Episode(
  val id: String,
  val title: String,
  val audioUrl: String
)

