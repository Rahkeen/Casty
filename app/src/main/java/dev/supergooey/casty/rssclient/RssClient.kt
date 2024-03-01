package dev.supergooey.casty.rssclient

import com.prof18.rssparser.RssParser
import com.prof18.rssparser.model.RssChannel

class RssClient {
  private val parser = RssParser()

  suspend fun getRssFeed(url: String): RssChannel {
    return parser.getRssChannel(url)
  }
}
