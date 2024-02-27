package dev.supergooey.casty.features.player.ui

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.supergooey.casty.features.player.domain.PodcastPlayerScreen
import dev.supergooey.casty.podcasts.PodcastRepository

class PodcastPlayerPresenter(
  private val podcastRepository: PodcastRepository
) : Presenter<PodcastPlayerScreen.State> {
  class Factory(private val podcastRepository: PodcastRepository) : Presenter.Factory {
    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return when (screen) {
        PodcastPlayerScreen -> {
          PodcastPlayerPresenter(
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
    val podcast = podcastRepository.getPodcast(0)
    return PodcastPlayerScreen.State(
      podcast = podcast
    ) { event -> }
  }
}