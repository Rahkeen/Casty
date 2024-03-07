package dev.supergooey.casty.data.media

import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackMediaSession : MediaSessionService() {

  private var mediaSession: MediaSession? = null

  @UnstableApi
  override fun onCreate() {
    super.onCreate()
    val player = ExoPlayer
      .Builder(this)
      .setSeekForwardIncrementMs(10000L)
      .setSeekBackIncrementMs(10000L)
      .build()

    mediaSession = MediaSession.Builder(this, player).build()

  }

  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
    return mediaSession
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    val player = mediaSession?.player!!
    if (!player.playWhenReady || player.mediaItemCount == 0) {
      stopSelf()
    }
  }

  override fun onDestroy() {
    mediaSession?.run {
      player.release()
      release()
      mediaSession = null
    }

    super.onDestroy()
  }
}