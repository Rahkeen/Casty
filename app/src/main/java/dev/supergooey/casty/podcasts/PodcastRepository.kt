package dev.supergooey.casty.podcasts

import com.prof18.rssparser.model.RssChannel
import dev.supergooey.casty.rssclient.RssClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias LocalPodcastStore = LinkedHashMap<String, Podcast>

interface PodcastRepository {
  fun getPodcasts(): List<Podcast>
  fun getPodcast(id: String): Podcast
  suspend fun fetchPodcast(url: String): Podcast
  fun selectEpisode(podcastId: String, episodeId: String): Episode
}

class RealPodcastRepository: PodcastRepository {

  private val rssClient = RssClient()
  private val localStore = LocalPodcastStore()

  override fun getPodcasts(): List<Podcast> {
    return localStore.values.toList()
  }

  override fun getPodcast(id: String): Podcast {
    return localStore.getValue(id)
  }

  override suspend fun fetchPodcast(url: String): Podcast {
    return withContext(Dispatchers.IO) {
      val podcast = rssClient.getRssFeed(url).toPodcast()
      localStore[podcast.name] = podcast
      podcast
    }
  }

  override fun selectEpisode(podcastId: String, episodeId: String): Episode {
    val episode = localStore.getValue(podcastId).episodes.find { it.id == episodeId }
    return episode!!
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
      )
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

