package ktor.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.micrometer.core.instrument.binder.db.PostgreSQLDatabaseMetrics
import io.micrometer.core.instrument.binder.jvm.*
import io.micrometer.core.instrument.binder.logging.LogbackMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.slf4j.event.Level
import javax.sql.DataSource

fun Application.configureMonitoring(
    meterRegistry: PrometheusMeterRegistry,
    dataSource: DataSource,
) {
    install(MicrometerMetrics) {
        registry = meterRegistry
        meterBinders = listOf(
            ClassLoaderMetrics(),
            JvmMemoryMetrics(),
            JvmGcMetrics(),
            JvmHeapPressureMetrics(),
            ProcessorMetrics(),
            JvmThreadMetrics(),
            FileDescriptorMetrics(),
            UptimeMetrics(),
            LogbackMetrics(),
            PostgreSQLDatabaseMetrics(dataSource, "awesome_kotlin"),
        )
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    routing {
        get("/metrics-micrometer") {
            call.respond(meterRegistry.scrape())
        }
    }
}
