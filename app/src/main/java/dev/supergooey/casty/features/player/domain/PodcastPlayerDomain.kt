package dev.supergooey.casty.features.player.domain

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.supergooey.casty.podcasts.Episode
import dev.supergooey.casty.podcasts.Podcast
import kotlinx.parcelize.Parcelize

@Parcelize
data class PodcastPlayerScreen(val podcastId: String, val episodeId: String) : Screen {
  data class State(
    val episode: EpisodeState,
    val isPlaying: Boolean,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed class Event : CircuitUiEvent {
    data object Play : Event()
    data object Pause : Event()
    data object Rewind: Event()
    data object FastForward: Event()
  }
}

data class EpisodeState(
  val id: String,
  val title: String,
  val audioUrl: String,
  val imageUrl: String
)
