package dev.supergooey.casty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.supergooey.casty.design.theme.CastyTheme
import dev.supergooey.casty.features.player.ui.PodcastPlayerPresenter
import dev.supergooey.casty.features.player.domain.PodcastPlayerScreen
import dev.supergooey.casty.features.player.ui.PodcastPlayer
import dev.supergooey.casty.podcasts.PodcastRepository

class MainActivity : ComponentActivity() {
  private val podcastRepository = PodcastRepository()
  private val circuit: Circuit = Circuit.Builder()
    .addPresenterFactory(PodcastPlayerPresenter.Factory(podcastRepository))
    .addUi<PodcastPlayerScreen, PodcastPlayerScreen.State> { state, _ ->
      PodcastPlayer(state = state)
    }
    .build()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val backstack = rememberSaveableBackStack(root = PodcastPlayerScreen)
      val navigator = rememberCircuitNavigator(backStack = backstack) {}

      CastyTheme {
        CircuitCompositionLocals(circuit = circuit) {
          NavigableCircuitContent(navigator = navigator, backStack = backstack)
        }
      }
    }
  }
}

