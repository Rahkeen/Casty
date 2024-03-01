package dev.supergooey.casty.data.rssclient

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel
import com.squareup.anvil.annotations.ContributesTo
import dev.supergooey.casty.di.AppScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RssClient @Inject constructor() {
  private val parser = RssParser()

  suspend fun getRssFeed(url: String): RssChannel {
    return parser.getRssChannel(url)
  }
}
