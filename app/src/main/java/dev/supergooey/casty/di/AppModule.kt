package dev.supergooey.casty.di

import android.app.Application
import android.content.Context
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@ContributesTo(AppScope::class)
@Module
abstract class AppModule {

  @Binds
  @Singleton
  abstract fun providesApplicationContext(app: Application): Context
}