package dev.supergooey.casty.di

import com.slack.circuit.foundation.Circuit
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.supergooey.casty.data.media.MediaClient
import dev.supergooey.casty.data.podcasts.PodcastRepository
import dev.supergooey.casty.features.downloader.domain.AddPodcastScreen
import dev.supergooey.casty.features.downloader.ui.AddPodcast
import dev.supergooey.casty.features.downloader.ui.AddPodcastPresenter
import dev.supergooey.casty.features.player.domain.PodcastPlayerScreen
import dev.supergooey.casty.features.player.ui.PodcastPlayer
import dev.supergooey.casty.features.player.ui.PodcastPlayerPresenter


@ContributesTo(AppScope::class)
@Module
object CircuitModule {

  @Provides
  fun providesCircuit(
    repository: PodcastRepository,
    mediaClient: MediaClient,
  ): Circuit {
    return Circuit.Builder()
      .addPresenterFactory(PodcastPlayerPresenter.Factory(repository, mediaClient))
      .addUi<PodcastPlayerScreen, PodcastPlayerScreen.State> { state, _ ->
        PodcastPlayer(state = state)
      }
      .addPresenterFactory(AddPodcastPresenter.Factory(repository))
      .addUi<AddPodcastScreen, AddPodcastScreen.State> { state, _ ->
        AddPodcast(state = state)
      }
      .build()
  }
}