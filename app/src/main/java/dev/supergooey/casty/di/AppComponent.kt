package dev.supergooey.casty.di

import android.app.Activity
import android.app.Application
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dev.supergooey.casty.CastyApplication
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@MergeComponent(AppScope::class)
interface AppComponent {

  fun inject(application: CastyApplication)

  @Component.Factory
  fun interface Factory {
    fun create(@BindsInstance context: Application): AppComponent
  }
}