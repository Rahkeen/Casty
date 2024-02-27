package dev.supergooey.casty.features.player.domain

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.supergooey.casty.podcasts.Podcast
import kotlinx.parcelize.Parcelize

@Parcelize
data object PodcastPlayerScreen : Screen {
  data class State(
    val podcast: Podcast,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed class Event : CircuitUiEvent {}
}
