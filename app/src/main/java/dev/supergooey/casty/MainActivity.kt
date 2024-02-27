package dev.supergooey.casty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.supergooey.casty.podcasts.PodcastRepository
import dev.supergooey.casty.ui.theme.CastyTheme
import kotlinx.parcelize.Parcelize

class MainActivity : ComponentActivity() {
  private val podcastRepository = PodcastRepository()
  private val circuit: Circuit = Circuit.Builder()
    .addPresenterFactory(PodcastSelectionPresenter.Factory(podcastRepository))
    .addUi<PodcastSelectionScreen, PodcastSelectionScreen.State> { state, _ ->
      PodcastSelection(state = state)
    }
    .addPresenterFactory(PodcastDetailsPresenter.Factory(podcastRepository))
    .addUi<PodcastDetailsScreen, PodcastDetailsScreen.State> { state, _ ->
      PodcastDetails(state = state)
    }
    .build()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val backstack = rememberSaveableBackStack(root = PodcastSelectionScreen)
      val navigator = rememberCircuitNavigator(backStack = backstack) {}

      CastyTheme {
        CircuitCompositionLocals(circuit = circuit) {
          NavigableCircuitContent(navigator = navigator, backStack = backstack)
        }
      }
    }
  }
}

@Parcelize
data object PodcastSelectionScreen : Screen {
  data class State(
    val podcasts: List<Podcast>,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed class Event : CircuitUiEvent {
    data class PodcastSelected(val podcastId: Int) : Event()
  }
}

data class Podcast(
  val id: Int,
  val imageUrl: String,
  val title: String
)

@Composable
fun PodcastSelection(state: PodcastSelectionScreen.State) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.statusBars),
    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
  ) {
    state.podcasts.forEach { podcast ->
      PodcastItem(
        podcast = podcast,
        action = { state.eventSink(PodcastSelectionScreen.Event.PodcastSelected(podcast.id)) })
    }
  }
}

@Composable
private fun PodcastItem(podcast: Podcast, action: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable {
        action()
      },
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    AsyncImage(
      model = podcast.imageUrl,
      modifier = Modifier.size(64.dp),
      contentScale = ContentScale.Crop,
      contentDescription = podcast.title
    )
    Text(
      modifier = Modifier.weight(1f),
      text = podcast.title
    )
  }
}

class PodcastSelectionPresenter(
  private val navigator: Navigator,
  private val podcastRepository: PodcastRepository
) : Presenter<PodcastSelectionScreen.State> {
  class Factory(private val podcastRepository: PodcastRepository) : Presenter.Factory {
    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return when (screen) {
        PodcastSelectionScreen -> {
          PodcastSelectionPresenter(
            navigator = navigator,
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
  override fun present(): PodcastSelectionScreen.State {
    val podcasts = podcastRepository.getPodcasts()
    return PodcastSelectionScreen.State(
      podcasts = podcasts
    ) { event ->
      when (event) {
        is PodcastSelectionScreen.Event.PodcastSelected -> {
          navigator.goTo(PodcastDetailsScreen(event.podcastId))
        }
      }
    }
  }
}

// Podcast Details Screen
@Parcelize
data class PodcastDetailsScreen(val podcastId: Int) : Screen {
  data class State(
    val podcast: Podcast,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState
  sealed class Event: CircuitUiEvent {
    data object Back : Event()
  }
}

@Composable
private fun PodcastDetails(state: PodcastDetailsScreen.State) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    AsyncImage(
      modifier = Modifier
        .fillMaxSize(.5f)
        .aspectRatio(1f),
      model = state.podcast.imageUrl,
      contentDescription = state.podcast.title
    )
    Text(text = state.podcast.title, style = TextStyle(fontSize = 24.sp))
  }

  BackHandler {
    state.eventSink(PodcastDetailsScreen.Event.Back)
  }
}

class PodcastDetailsPresenter(
  private val navigator: Navigator,
  private val screen: PodcastDetailsScreen,
  private val podcastRepository: PodcastRepository,
) : Presenter<PodcastDetailsScreen.State> {

  class Factory(private val podcastRepository: PodcastRepository) : Presenter.Factory {
    override fun create(
      screen: Screen,
      navigator: Navigator,
      context: CircuitContext
    ): Presenter<*>? {
      return when (screen) {
        is PodcastDetailsScreen -> {
          PodcastDetailsPresenter(
            navigator = navigator,
            screen = screen,
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
  override fun present(): PodcastDetailsScreen.State {
    val podcast = podcastRepository.getPodcast(screen.podcastId)
    return PodcastDetailsScreen.State(podcast) { event ->
      when (event) {
        PodcastDetailsScreen.Event.Back -> {
          navigator.pop()
        }
      }
    }
  }
}