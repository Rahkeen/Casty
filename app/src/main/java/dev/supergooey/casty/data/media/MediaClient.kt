package dev.supergooey.casty.data.media

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors

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
  }
}