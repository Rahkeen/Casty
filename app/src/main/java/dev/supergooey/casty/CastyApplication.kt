package dev.supergooey.casty

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.slack.circuit.foundation.Circuit
import dev.supergooey.casty.data.db.CastyDatabase
import dev.supergooey.casty.data.podcasts.PodcastRepository
import dev.supergooey.casty.data.podcasts.RealPodcastRepository
import dev.supergooey.casty.data.rssclient.RssClient
import dev.supergooey.casty.features.downloader.domain.AddPodcastScreen
import dev.supergooey.casty.features.downloader.ui.AddPodcast
import dev.supergooey.casty.features.downloader.ui.AddPodcastPresenter
import dev.supergooey.casty.features.player.domain.PodcastPlayerScreen
import dev.supergooey.casty.features.player.ui.PodcastPlayer
import dev.supergooey.casty.features.player.ui.PodcastPlayerPresenter

class CastyApplication: Application() {
  lateinit var podcastRepository: PodcastRepository
  lateinit var circuit: Circuit

  override fun onCreate() {
    super.onCreate()

    val db = Room.databaseBuilder(
      applicationContext,
      CastyDatabase::class.java,
      "castydb"
    ).build()

    val rssClient = RssClient()
    val podcastDao = db.podcastDao()
    val episodeDao = db.episodeDao()

    podcastRepository = RealPodcastRepository(
      podcastDao,
      episodeDao,
      rssClient
    )

     circuit = Circuit.Builder()
      .addPresenterFactory(PodcastPlayerPresenter.Factory(podcastRepository))
      .addUi<PodcastPlayerScreen, PodcastPlayerScreen.State> { state, _ ->
        PodcastPlayer(state = state)
      }
      .addPresenterFactory(AddPodcastPresenter.Factory(podcastRepository))
      .addUi<AddPodcastScreen, AddPodcastScreen.State> { state, _ ->
        AddPodcast(state = state)
      }
      .build()
  }
}

fun Context.castyApplication() = this.applicationContext as CastyApplication
