package dev.supergooey.casty.features.downloader.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import dev.supergooey.casty.R
import dev.supergooey.casty.design.theme.CastyTheme
import dev.supergooey.casty.design.theme.Purple40
import dev.supergooey.casty.design.theme.Purple80
import dev.supergooey.casty.features.downloader.domain.AddPodcastScreen
import dev.supergooey.casty.features.downloader.domain.PodcastState
import dev.supergooey.casty.podcasts.Episode
import dev.supergooey.casty.podcasts.Podcast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddPodcast(state: AddPodcastScreen.State) {
  val keyboardController = LocalSoftwareKeyboardController.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(vertical = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      contentAlignment = Alignment.Center
    ) {
      when (val podcastState = state.podcastState) {
        is PodcastState.Album -> {
          PodcastAlbumCover(
            podcast = podcastState.podcast,
            selectEpisode = { episode ->
              state.eventSink(AddPodcastScreen.Event.SelectEpisode(podcastState.podcast, episode))
            }
          )
        }

        PodcastState.None -> {}
      }
    }
    TextField(
      value = state.requestUrl,
      colors = TextFieldDefaults.colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        focusedContainerColor = Color.LightGray,
        unfocusedContainerColor = Color.LightGray,
        cursorColor = Color.Black,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black
      ),
      shape = RoundedCornerShape(8.dp),
      onValueChange = {
        state.eventSink(AddPodcastScreen.Event.EditSearch(query = it))
      },
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
      keyboardActions = KeyboardActions(
        onGo = {
          state.eventSink(AddPodcastScreen.Event.RequestPodcast(url = state.requestUrl))
          keyboardController?.hide()
        }
      )
    )
  }
}


@Preview
@Composable
private fun AddPodcastPreview() {
  CastyTheme {
    AddPodcast(
      state = AddPodcastScreen.State(
        requestUrl = "https://feeds.transistor.fm/mostly-technical",
        podcastState = PodcastState.Album(Podcast(name = "Mostly Technical", imageUrl = ""))
      ) {}
    )
  }
}

@Preview
@Composable
private fun PodcastAlbumCoverPreview() {
  CastyTheme {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      PodcastAlbumCover(
        podcast = Podcast(
          name = "Mostly Technical",
          imageUrl = "https://images.transistor.fm/images/show/43677/full_1691582852-artwork.jpg",
          episodes = listOf(
            Episode(
              id = "1",
              title = "What the Eff?",
              audioUrl = ""
            ),
            Episode(
              id = "2",
              title = "Holy Guacamole",
              audioUrl = ""
            ),
            Episode(
              id = "3",
              title = "Animation Basics",
              audioUrl = ""
            ),
          )
        ),
        selectEpisode = {}
      )
    }
  }
}

@Composable
private fun PodcastAlbumCover(
  modifier: Modifier = Modifier,
  podcast: Podcast,
  selectEpisode: (Episode) -> Unit
) {
  val scope = rememberCoroutineScope()
  val albumAnimationDuration = remember { 300 }
  val discOffset = remember { Animatable(0f) }
  val discRotation = remember { Animatable(0f) }

  var showEpisodes by remember { mutableStateOf(false) }
  val albumSize by animateDpAsState(
    targetValue = if (showEpisodes) 300.dp else 300.dp,
    animationSpec = tween(
      durationMillis = 500,
      easing = EaseInOutCubic
    ),
    label = "Album Size"
  )
  val albumRotation by animateFloatAsState(
    targetValue = if (showEpisodes) 180f else 0f,
    animationSpec = tween(
      durationMillis = albumAnimationDuration,
      easing = EaseInOut
    ),
    label = "Album Rotation"
  )
  val albumZIndex by animateFloatAsState(
    targetValue = if (showEpisodes) 0f else 1f,
    animationSpec = tween(
      durationMillis = albumAnimationDuration,
      easing = EaseInOut
    ),
    label = "Album Z Index"
  )

  LaunchedEffect(Unit) {
    delay(500)
    launch {
      discOffset.animateTo(
        targetValue = -150f,
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioLowBouncy,
          stiffness = Spring.StiffnessLow
        )
      )
    }
    launch {
      discRotation.animateTo(
        targetValue = (-45..45).random().toFloat(),
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioLowBouncy,
          stiffness = Spring.StiffnessLow
        )
      )
    }
  }
  Box(
    modifier = modifier
      .wrapContentSize()
      .graphicsLayer { clip = false }
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        showEpisodes = !showEpisodes
        if (showEpisodes) {
          scope.launch {
            discOffset.animateTo(0f)
          }
        } else {
          scope.launch {
            delay(albumAnimationDuration.toLong())
            discOffset.animateTo(
              targetValue = -150f,
              animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
              )
            )
          }
        }
      },
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .zIndex(-1f)
        .size(albumSize)
        .offset(y = discOffset.value.dp)
        .graphicsLayer {
          rotationZ = discRotation.value
          rotationY = albumRotation
        }
        .drawBehind {
          val discWidth = this.size.width * 0.95f
          drawCircle(color = Color.Black, radius = discWidth / 2f)
          repeat(14) { index ->
            drawCircle(
              brush = Brush.linearGradient(
                colors = listOf(Color.DarkGray, Color.Black),
              ),
              radius = discWidth * (0.2f + (0.01f * index)),
              style = Stroke(width = 2f)
            )
          }

          repeat(10) { index ->
            drawCircle(
              brush = Brush.linearGradient(
                colors = listOf(Color.DarkGray, Color.Black),
              ),
              radius = discWidth * (0.4f + (0.01f * index)),
              style = Stroke(width = 2f)
            )
          }
        },
      contentAlignment = Alignment.Center
    ) {
      AsyncImage(
        model = podcast.imageUrl,
        modifier = Modifier
          .fillMaxWidth(0.4f)
          .aspectRatio(1f)
          .clip(CircleShape),
        contentScale = ContentScale.Crop,
        contentDescription = "album art"
      )
    }
    AsyncImage(
      model = podcast.imageUrl,
      modifier = Modifier
        .zIndex(albumZIndex)
        .graphicsLayer {
          rotationY = albumRotation
        }
        .size(albumSize)
        .clip(RoundedCornerShape(6.dp))
        .background(color = Color.DarkGray),
      contentScale = ContentScale.Crop,
      contentDescription = "Album Art"
    )
    val scrollState = rememberScrollState()
    Column(
      modifier = Modifier
        .zIndex(1f - albumZIndex)
        .graphicsLayer {
          rotationY = -180f + albumRotation
        }
        .size(albumSize)
        .clip(RoundedCornerShape(6.dp))
        .verticalScroll(scrollState)
        .background(color = Color.DarkGray)
        .padding(32.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.Start
    ) {
      podcast.episodes.takeLast(10).forEach { episode ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
              // navigate to player
              selectEpisode(episode)
            },
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            modifier = Modifier.weight(1f),
            text = episode.title,
            fontSize = 12.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Icon(
            painter = painterResource(id = R.drawable.ic_play),
            tint = Color.White,
            contentDescription = ""
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun FlipperCard() {
  var toggle by remember {
    mutableStateOf(false)
  }

  val rotation by animateFloatAsState(
    targetValue = if (toggle) 180f else 0f,
    animationSpec = tween(durationMillis = 500),
    label = "Album Rotation"
  )

  val zIndex by animateFloatAsState(
    targetValue = if (toggle) 0f else 1f,
    animationSpec = tween(durationMillis = 500),
    label = "Album Rotation"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .clickable { toggle = !toggle },
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .graphicsLayer {
          rotationY = -180f + rotation
        }
        .zIndex(1f - zIndex)
        .size(300.dp)
        .background(color = Purple40),
      contentAlignment = Alignment.Center
    ) {
      Text("Back", style = TextStyle(fontSize = 24.sp, color = Color.White))
    }
    Box(
      modifier = Modifier
        .graphicsLayer {
          rotationY = rotation
        }
        .zIndex(zIndex)
        .size(300.dp)
        .background(color = Purple80),
      contentAlignment = Alignment.Center
    ) {
      Text("Front", style = TextStyle(fontSize = 24.sp))
    }
  }
}
