package dev.supergooey.casty.features.downloader.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.supergooey.casty.features.downloader.domain.AddPodcastScreen
import dev.supergooey.casty.features.downloader.domain.PodcastState
import dev.supergooey.casty.features.player.domain.PodcastPlayerScreen
import dev.supergooey.casty.podcasts.PodcastRepository
import kotlinx.coroutines.launch

class AddPodcastPresenter(
  private val podcastRepository: PodcastRepository,
  private val navigator: Navigator
): Presenter<AddPodcastScreen.State> {
  class Factory(private val podcastRepository: PodcastRepository): Presenter.Factory {
    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return when (screen) {
        AddPodcastScreen -> {
          AddPodcastPresenter(
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
  override fun present(): AddPodcastScreen.State {
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var podcastState by remember { mutableStateOf<PodcastState>(PodcastState.None) }

    fun events(event: AddPodcastScreen.Event) {
      when (event) {
        is AddPodcastScreen.Event.EditSearch -> {
          query = event.query
        }

        is AddPodcastScreen.Event.RequestPodcast -> {
          scope.launch {
            val result = podcastRepository.fetchPodcast(event.url)
            podcastState = PodcastState.Album(result)
          }
        }

        is AddPodcastScreen.Event.SelectEpisode -> {
          navigator.goTo(PodcastPlayerScreen(event.podcast.name, event.episode.id))
        }
      }
    }

    return AddPodcastScreen.State(
      requestUrl = query,
      podcastState = podcastState,
      eventSink = ::events
    )
  }
}