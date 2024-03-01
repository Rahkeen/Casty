package dev.supergooey.casty

import android.app.Activity
import android.app.AppComponentFactory
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.slack.circuit.foundation.Circuit
import dagger.internal.DaggerCollections
import dagger.internal.DaggerGenerated
import dev.supergooey.casty.data.db.CastyDatabase
import dev.supergooey.casty.data.podcasts.PodcastRepository
import dev.supergooey.casty.data.podcasts.RealPodcastRepository
import dev.supergooey.casty.data.rssclient.RssClient
import dev.supergooey.casty.di.AppComponent
import dev.supergooey.casty.di.DaggerAppComponent
import dev.supergooey.casty.features.downloader.domain.AddPodcastScreen
import dev.supergooey.casty.features.downloader.ui.AddPodcast
import dev.supergooey.casty.features.downloader.ui.AddPodcastPresenter
import dev.supergooey.casty.features.player.domain.PodcastPlayerScreen
import dev.supergooey.casty.features.player.ui.PodcastPlayer
import dev.supergooey.casty.features.player.ui.PodcastPlayerPresenter
import javax.inject.Inject

class CastyApplication: Application() {

  @Inject
  lateinit var circuit: Circuit

  private lateinit var component: AppComponent

  override fun onCreate() {
    super.onCreate()

    component = DaggerAppComponent.factory().create(this).also { it.inject(this) }
  }
}

fun Context.castyApplication() = this.applicationContext as CastyApplication
