package dev.supergooey.casty.features.player.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.supergooey.casty.features.player.domain.EpisodeState
import dev.supergooey.casty.features.player.domain.PodcastPlayerScreen
import dev.supergooey.casty.podcasts.Episode
import dev.supergooey.casty.podcasts.PodcastRepository
import java.util.UUID

class PodcastPlayerPresenter(
  private val screen: PodcastPlayerScreen,
  private val podcastRepository: PodcastRepository
) : Presenter<PodcastPlayerScreen.State> {
  class Factory(private val podcastRepository: PodcastRepository) : Presenter.Factory {
    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return when (screen) {
        is PodcastPlayerScreen -> {
          PodcastPlayerPresenter(
            screen = screen,
            podcastRepository = podcastRepository
          )
        }

        else -> {
          null
        }
      }
    }
  }

  @Composable
  override fun present(): PodcastPlayerScreen.State {
    var isPlaying by remember { mutableStateOf(false) }
    val podcast = remember { podcastRepository.getPodcast(screen.podcastId) }
    val episode =
      remember { podcastRepository.selectEpisode(screen.podcastId, screen.episodeId) }
    val episodeState by remember {
      mutableStateOf(
        EpisodeState(
          episode.id,
          episode.title,
          episode.audioUrl,
          podcast.imageUrl
        )
      )
    }

    return PodcastPlayerScreen.State(
      episode = episodeState,
      isPlaying = isPlaying
    ) { event ->
      when (event) {
        PodcastPlayerScreen.Event.Pause -> {
          isPlaying = false
        }

        PodcastPlayerScreen.Event.Play -> {
          isPlaying = true
        }

        PodcastPlayerScreen.Event.FastForward -> {
        }

        PodcastPlayerScreen.Event.Rewind -> {
        }
      }
    }
  }
}