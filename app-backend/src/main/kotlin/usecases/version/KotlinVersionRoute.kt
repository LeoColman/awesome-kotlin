package usecases.version

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktor.KtorRoute

class KotlinVersionRoute(
    private val kotlinVersionFetcher: KotlinVersionFetcher,
) : KtorRoute  {
    override fun Routing.install() {
        get("/api/kotlin-versions") {
            call.respond(kotlinVersionFetcher.getLatestVersions(listOf("1.8", "1.9")))
        }
    }
}
