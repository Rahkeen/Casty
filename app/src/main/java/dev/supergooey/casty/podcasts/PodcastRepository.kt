package dev.supergooey.casty.podcasts

import dev.supergooey.casty.Podcast
import kotlinx.coroutines.coroutineScope

class PodcastRepository {
  private val podcasts = listOf(
    Podcast(
      id = 0,
      imageUrl = "https://i.pinimg.com/564x/a5/18/15/a51815aca740584469215f0a6cc85813.jpg",
      title = "Riffy in a Jiffy"
    ),
    Podcast(
      id = 1,
      imageUrl = "https://i.pinimg.com/564x/a5/18/15/a51815aca740584469215f0a6cc85813.jpg",
      title = "Duffy in a Buffy"
    ),
    Podcast(
      id = 2,
      imageUrl = "https://i.pinimg.com/564x/a5/18/15/a51815aca740584469215f0a6cc85813.jpg",
      title = "Boofy in a Hoofy"
    ),
  )
  fun getPodcasts(): List<Podcast> {
    return podcasts
  }

  fun getPodcast(id: Int): Podcast {
    return podcasts.first { it.id == id }
  }
}