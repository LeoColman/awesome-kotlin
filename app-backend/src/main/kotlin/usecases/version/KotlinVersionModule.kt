package usecases.version

import HttpClientModule
import XmlModule
import di.bean

open class KotlinVersionModule(
    private val xmlModule: XmlModule,
    private val httpClientModule: HttpClientModule,
) {
    open val kotlinVersionFetcher by bean<KotlinVersionFetcher> {
        CachedVersionFetcher(
            kotlinVersionFetcher = MavenCentralKotlinVersionFetcher(
                xmlMapper = xmlModule.xmlMapper.get,
                httpClient = httpClientModule.httpClient.get,
            )
        )
    }

    open val route by bean {
        KotlinVersionRoute(
            kotlinVersionFetcher = kotlinVersionFetcher.get,
        )
    }
}
