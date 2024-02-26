package lifecycle

import di.bean

open class LifecycleModule {
    open val shutdownHandler by bean<ShutdownManager> {
        JvmShutdownManager()
    }

    open val gracefulShutdown by bean {
        GracefulShutdown().also {
            shutdownHandler.get.addHandler(it::shutdown)
        }
    }
}
