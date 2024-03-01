@file:OptIn(ExperimentalMaterial3Api::class)

package dev.supergooey.casty.features.player.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import dev.supergooey.casty.R
import dev.supergooey.casty.design.theme.CastyTheme
import dev.supergooey.casty.features.player.domain.EpisodeState
import dev.supergooey.casty.features.player.domain.PodcastPlayerScreen
import me.saket.squiggles.SquigglySlider

@Composable
fun PodcastPlayer(state: PodcastPlayerScreen.State) {
  val context = LocalContext.current
  val player = remember { ExoPlayer.Builder(context).build() }

  LaunchedEffect(state.episode.id) {
    player.setMediaItem(MediaItem.fromUri(state.episode.audioUrl))
    player.prepare()
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    SpinningDisc(state.episode.imageUrl)
    SquigglySlider(
      colors = SliderDefaults.colors(
        thumbColor = Color.Black,
        activeTrackColor = Color.Black,
        inactiveTrackColor = Color.Black
      ),
      value = 0.5f,
      onValueChange = {},
      squigglesSpec = SquigglySlider.SquigglesSpec(
        strokeWidth = 4.dp,
        wavelength = 24.dp,
        amplitude = if (state.isPlaying) 2.dp else 0.dp
      )
    )

    PodcastControls(
      isPlaying = state.isPlaying,
      actions = { action ->
        when (action) {
          PodcastPlayerScreen.Event.Pause -> {
            player.pause()
          }
          PodcastPlayerScreen.Event.Play -> {
            player.play()
          }
          else -> {}
        }
        state.eventSink(action)
      }
    )
  }
}

@Preview
@Composable
private fun PodcastPlayerPreview() {
  PodcastPlayer(
    state = PodcastPlayerScreen.State(
      episode = EpisodeState(
        id = "dead-beef-dead-beef",
        title = "What's the Good Word?",
        audioUrl = "https://audio.transistor.fm/m/shows/43677/f86bb806f8390c44ea9e4eb475e80c0f.mp3",
        imageUrl = "https://images.transistor.fm/images/show/43677/full_1691582852-artwork.jpg"
      ),
      isPlaying = false
    ) {}
  )
}

@Preview
@Composable
private fun PodcastControlsPreview() {
  CastyTheme {
    PodcastControls(false, {})
  }
}

@Composable
private fun PodcastControls(
  isPlaying: Boolean,
  actions: (PodcastPlayerScreen.Event) -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(
      space = 16.dp,
      alignment = Alignment.CenterHorizontally
    ),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .clickable {
          actions(PodcastPlayerScreen.Event.Rewind)
        },
      painter = painterResource(id = R.drawable.ic_rewind),
      contentDescription = "rewind"
    )
    Icon(
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .clickable {
          val event = if (isPlaying) {
            PodcastPlayerScreen.Event.Pause
          } else {
            PodcastPlayerScreen.Event.Play
          }
          actions(event)
        },
      painter = if (isPlaying) {
        painterResource(id = R.drawable.ic_pause)
      } else {
        painterResource(id = R.drawable.ic_play)
      },
      contentDescription = "play"
    )
    Icon(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .clickable {
          actions(PodcastPlayerScreen.Event.FastForward)
        },
      painter = painterResource(id = R.drawable.ic_fastforward),
      contentDescription = "fastforward"
    )
  }
}

@Preview
@Composable
private fun SpinningDiscPreview() {
  CastyTheme {
    SpinningDisc(imageUrl = "")
  }
}

@Composable
private fun SpinningDisc(imageUrl: String) {
  val infinite = rememberInfiniteTransition(label = "SpinningDisc")
  val discRotation by infinite.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = 10000,
        easing = LinearEasing

      )
    )
    , label = "SpinningDisc_Rotation"
  )
  Box(
    modifier = Modifier
      .graphicsLayer {
        rotationZ = discRotation
      }
      .fillMaxWidth()
      .aspectRatio(1f)
      .drawBehind {
        drawCircle(Color.Black)
        repeat(14) { index ->
          drawCircle(
            brush = Brush.linearGradient(
              colors = listOf(Color.DarkGray, Color.Black),
            ),
            radius = size.width * (0.2f + (0.01f * index)),
            style = Stroke(width = 2f)
          )
        }

        repeat(10) { index ->
          drawCircle(
            brush = Brush.linearGradient(
              colors = listOf(Color.DarkGray, Color.Black),
            ),
            radius = size.width * (0.4f + (0.01f * index)),
            style = Stroke(width = 2f)
          )
        }
      },
    contentAlignment = Alignment.Center
  ) {
    AsyncImage(
      modifier = Modifier
        .fillMaxWidth(0.4f)
        .aspectRatio(1f)
        .clip(CircleShape),
      model = imageUrl,
      contentScale = ContentScale.Crop,
      contentDescription = "album art"
    )
  }
}

