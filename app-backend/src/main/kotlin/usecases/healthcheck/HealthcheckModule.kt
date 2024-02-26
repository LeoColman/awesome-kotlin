package usecases.healthcheck

import di.bean

open class HealthcheckModule {
    open val route by bean {
        HealthCheckRoute()
    }
}
