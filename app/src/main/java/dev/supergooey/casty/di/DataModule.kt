package dev.supergooey.casty.di

import android.content.Context
import androidx.room.Room
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.supergooey.casty.data.db.CastyDatabase
import dev.supergooey.casty.data.db.EpisodeDao
import dev.supergooey.casty.data.db.PodcastDao
import javax.inject.Singleton

@ContributesTo(AppScope::class)
@Module
object DataModule {

  @Provides
  @Singleton
  fun providesDatabase(context: Context): CastyDatabase {
    return Room.databaseBuilder(
      context,
      CastyDatabase::class.java,
      "castydb"
    ).build()
  }

  @Provides
  @Singleton
  fun providesPodcastDao(db: CastyDatabase): PodcastDao {
    return db.podcastDao()
  }

  @Provides
  @Singleton
  fun providesEpisodeDao(db: CastyDatabase): EpisodeDao {
    return db.episodeDao()
  }
}