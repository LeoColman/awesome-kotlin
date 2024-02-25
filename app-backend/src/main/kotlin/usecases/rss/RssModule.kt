package usecases.rss

import di.bean

open class RssModule {
    open val rssRoute by bean {
        RssRoute()
    }

    open val fullRssRoute by bean {
        FullRssRoute()
    }
}
