package metrics

import JdbcModule
import di.bean
import io.micrometer.core.instrument.binder.db.PostgreSQLDatabaseMetrics
import io.micrometer.core.instrument.binder.jvm.*
import io.micrometer.core.instrument.binder.logging.LogbackMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry

open class MetricsModule(
    private val jdbcModule: JdbcModule,
) {
    open val meterRegistry by bean {
        PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    }

    open val binders by bean {
        listOf(
            ClassLoaderMetrics(),
            JvmMemoryMetrics(),
            JvmGcMetrics(),
            JvmHeapPressureMetrics(),
            ProcessorMetrics(),
            JvmThreadMetrics(),
            FileDescriptorMetrics(),
            UptimeMetrics(),
            LogbackMetrics(),
            PostgreSQLDatabaseMetrics(
                jdbcModule.dataSource.get,
                "awesome_kotlin"
            ),
        )
    }

    open val route by bean {
        MetricsRoute(
            meterRegistry = meterRegistry.get,
        )
    }
}
