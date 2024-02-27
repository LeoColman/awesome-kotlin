package usecases.stars_job

import di.bean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lifecycle.LifecycleModule
import utils.logger
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class StarsJob {
    fun run() {
        log.info("Hello, StarsJob!")
    }

    private companion object {
        val log = logger<StarsJob>()
    }
}

class StarsJobScheduler(
    private val starsJob: StarsJob,
    private val applicationScope: CoroutineScope,
) {
    fun start() {
        applicationScope.launch {
            delay(5.seconds)
            while (true) {
                log.info("About to run stars job")
                starsJob.run()
                log.info("Stars job finished, sleeping for 24 hours")
                delay(24.hours)
            }
        }
    }

    private companion object {
        val log = logger<StarsJobScheduler>()
    }
}

class StarsJobModule(
    private val lifecycleModule: LifecycleModule,
) {
    val starsJob by bean {
        StarsJob()
    }

    val starsJobScheduler by bean {
        StarsJobScheduler(
            starsJob = starsJob.get,
            applicationScope = lifecycleModule.applicationScope.get,
        )
    }
}
