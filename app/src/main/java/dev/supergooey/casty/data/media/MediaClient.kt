package dev.supergooey.casty.data.media

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

data class MediaProgress(
  val current: Long,
  val duration: Long
) {
  val percent = if (duration == 0L) 0F else current.toFloat() / duration
}

class MediaClient(context: Context) {

  private lateinit var mediaController: MediaController

  init {
    val sessionToken = SessionToken(context, ComponentName(context, PlaybackMediaSession::class.java))
    val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
    controllerFuture.addListener({
      mediaController = controllerFuture.get()
      mediaController.prepare()
    }, MoreExecutors.directExecutor())
  }

  fun progress() = callbackFlow {
    val listener = object : Player.Listener {
      override fun onEvents(player: Player, events: Player.Events) {
        val current = player.currentPosition
        val duration = player.duration
        trySend(MediaProgress(current, duration))
      }
    }

    mediaController.addListener(listener)
    awaitClose { mediaController.removeListener(listener) }
  }.buffer(capacity = Channel.UNLIMITED)

  fun loadEpisode(audioUrl: String) {
    val mediaItem = MediaItem.fromUri(audioUrl)
    mediaController.setMediaItem(mediaItem)
    mediaController.prepare()
  }

  fun play() {
    mediaController.play()
  }

  fun pause() {
    mediaController.pause()
  }

  fun rewind() {
    mediaController.seekBack()
  }

  fun forward() {
    mediaController.seekForward()
  }

  fun stop() {
    mediaController.stop()
    mediaController.clearMediaItems()
  }
}