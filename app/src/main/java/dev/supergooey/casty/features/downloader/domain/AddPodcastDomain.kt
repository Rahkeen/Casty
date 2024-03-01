package dev.supergooey.casty.features.downloader.domain

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.screen.Screen
import dev.supergooey.casty.podcasts.Episode
import dev.supergooey.casty.podcasts.Podcast
import kotlinx.parcelize.Parcelize

@Parcelize
data object AddPodcastScreen: Screen {
  data class State(
    val requestUrl: String,
    val podcastState: PodcastState,
    val eventSink: (Event) -> Unit
  ): CircuitUiState

  sealed class Event: CircuitUiEvent {
    data class EditSearch(val query: String): Event()
    data class RequestPodcast(val url: String): Event()
    data class SelectEpisode(val podcast: Podcast, val episode: Episode): Event()
  }
}

sealed class PodcastState {
  data object None: PodcastState()
  data class Album(val podcast: Podcast): PodcastState()
}