package dev.supergooey.casty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.LocalCircuit
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.supergooey.casty.design.theme.CastyTheme
import dev.supergooey.casty.features.downloader.domain.AddPodcastScreen
import dev.supergooey.casty.features.downloader.ui.AddPodcast
import dev.supergooey.casty.features.downloader.ui.AddPodcastPresenter
import dev.supergooey.casty.features.player.domain.PodcastPlayerScreen
import dev.supergooey.casty.features.player.ui.PodcastPlayer
import dev.supergooey.casty.features.player.ui.PodcastPlayerPresenter
import dev.supergooey.casty.data.podcasts.RealPodcastRepository
import dev.supergooey.casty.data.rssclient.RssClient

class MainActivity : ComponentActivity() {


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val backstack = rememberSaveableBackStack(root = AddPodcastScreen)
      val navigator = rememberCircuitNavigator(backStack = backstack) {}
      val circuit = LocalContext.current.castyApplication().circuit

      CastyTheme {
        CircuitCompositionLocals(circuit = circuit) {
          NavigableCircuitContent(navigator = navigator, backStack = backstack)
        }
      }
    }
  }
}

