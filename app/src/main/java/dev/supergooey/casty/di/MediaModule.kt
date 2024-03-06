package dev.supergooey.casty.di

import android.content.Context
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.supergooey.casty.data.media.MediaClient
import javax.inject.Singleton

@ContributesTo(AppScope::class)
@Module
object MediaModule {

  @Singleton
  @Provides
  fun providesMediaClient(context: Context): MediaClient {
    return MediaClient(context)
  }
}