package ktor

import metrics.MetricsModule
import di.bean
import ktor.features.*
import usecases.signup.JwtModule

open class KtorFeaturesModule(
    private val jwtModule: JwtModule,
    private val metricsModule: MetricsModule,
) {
    open val authenticationFeature by bean {
        AuthenticationFeature(
            jwtConfig = jwtModule.jwtConfig.get,
        )
    }

    open val cachingFeature by bean {
        CachingFeature()
    }

    open val contentNegotiationFeature by bean {
        ContentNegotiationFeature()
    }

    open val locationsFeature by bean {
        LocationsFeature()
    }

    open val statusPageFeature by bean {
        StatusPageFeature()
    }

    open val callLoggingFeature by bean {
        CallLoggingFeature()
    }

    open val metricsFeature by bean {
        MetricsFeature(
            meterRegistry = metricsModule.meterRegistry.get,
            binders = metricsModule.binders.get,
        )
    }

    open val features by bean {
        listOf(
            authenticationFeature.get,
            cachingFeature.get,
            contentNegotiationFeature.get,
            locationsFeature.get,
            statusPageFeature.get,
            callLoggingFeature.get,
            metricsFeature.get,
        )
    }
}
