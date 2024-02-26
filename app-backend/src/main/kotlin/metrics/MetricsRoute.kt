package metrics

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusMeterRegistry
import ktor.KtorRoute

class MetricsRoute(
    private val meterRegistry: PrometheusMeterRegistry,
) : KtorRoute {
    override fun Routing.install() {
        get("/metrics") {
            call.respondText(
                text = meterRegistry.scrape(),
                contentType = ContentType.Text.Plain
                    .withCharset(Charsets.UTF_8)
                    .withParameter("version", "0.0.4"),
            )
        }
    }
}
