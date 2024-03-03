package dev.supergooey.casty.features.player.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.supergooey.casty.features.player.domain.EpisodeState
import dev.supergooey.casty.features.player.domain.PodcastPlayerScreen
import dev.supergooey.casty.data.podcasts.PodcastRepository
import dev.supergooey.casty.features.downloader.domain.PodcastState
import kotlinx.coroutines.flow.map

class PodcastPlayerPresenter(
  private val screen: PodcastPlayerScreen,
  private val podcastRepository: PodcastRepository,
  private val navigator: Navigator
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
            podcastRepository = podcastRepository,
            navigator = navigator
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
    var episodeState by remember { mutableStateOf<EpisodeState>(EpisodeState.Loading) }

    LaunchedEffect(Unit) {
      val episode = podcastRepository.selectEpisode(screen.episodeId)
      episodeState = EpisodeState.Disc(
        id = episode.id,
        title = episode.title,
        audioUrl = episode.audioUrl,
        imageUrl = episode.albumArtUrl
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

        PodcastPlayerScreen.Event.BackPressed -> {
          isPlaying = false
          navigator.pop()
        }
      }
    }
  }
}