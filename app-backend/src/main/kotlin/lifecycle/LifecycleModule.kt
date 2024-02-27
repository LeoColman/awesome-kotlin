package lifecycle

import di.bean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

open class LifecycleModule {
    open val shutdownHandler by bean<ShutdownManager> {
        JvmShutdownManager()
    }

    open val gracefulShutdown by bean {
        GracefulShutdown().also {
            shutdownHandler.get.addHandler(it::shutdown)
        }
    }

    open val applicationScope by bean {
        val job = SupervisorJob()
        CoroutineScope(job).also {
            shutdownHandler.get.addHandler { job.cancel() }
        }
    }
}
