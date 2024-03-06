package dev.supergooey.casty.features.player.domain

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import kotlinx.parcelize.Parcelize

@Parcelize
data class PodcastPlayerScreen(val episodeId: String) : Screen {
  data class State(
    val episode: EpisodeState,
    val isPlaying: Boolean,
    val progress: Float,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed class Event : CircuitUiEvent {
    data object Play : Event()
    data object Pause : Event()
    data object Rewind: Event()
    data object FastForward: Event()
    data object BackPressed: Event()
  }
}

sealed class EpisodeState {
  data object Loading: EpisodeState()
  data class Disc(
    val id: String,
    val title: String,
    val audioUrl: String,
    val imageUrl: String
  ): EpisodeState()
}

