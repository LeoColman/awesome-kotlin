import config.ConfigModule
import di.bean
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import ktor.KtorFeaturesModule
import ktor.plugins.configureSockets
import lifecycle.LifecycleModule
import metrics.MetricsModule
import usecases.github.GithubModule
import usecases.kug.KugModule
import usecases.links.LinksModule
import usecases.healthcheck.HealthcheckModule
import usecases.rss.RssModule
import usecases.signup.LoginModule
import usecases.signup.RegisterModule
import usecases.version.KotlinVersionModule
import utils.withEach
import kotlin.time.Duration

open class ServerModule(
    private val githubModule: GithubModule,
    private val healthcheckModule: HealthcheckModule,
    private val loginModule: LoginModule,
    private val registerModule: RegisterModule,
    private val linksModule: LinksModule,
    private val kugModule: KugModule,
    private val metricsModule: MetricsModule,
    private val lifecycleModule: LifecycleModule,
    private val configModule: ConfigModule,
    private val rssModule: RssModule,
    private val kotlinVersionModule: KotlinVersionModule,
    private val ktorFeaturesModule: KtorFeaturesModule,
) {
    open val unauthenticatedRoutes by bean {
        listOf(
            githubModule.githubRedirectRoute.get,
            githubModule.githubCallbackRoute.get,

            healthcheckModule.route.get,

            metricsModule.route.get,

            loginModule.route.get,
            registerModule.route.get,

            linksModule.route.get,
            kugModule.getKugRoute.get,
            kugModule.updateKugsRoute.get,

            rssModule.rssRoute.get,
            rssModule.fullRssRoute.get,

            kotlinVersionModule.route.get,
        )
    }

    open val ktorServer by bean {
        System.setProperty("io.ktor.server.engine.ShutdownHook", "false")

        val features = ktorFeaturesModule.features.get
        val unauthenticatedRoutes = unauthenticatedRoutes.get
        val serverConfig = serverConfig.get

        embeddedServer(
            factory = CIO,
            port = serverConfig.port,
            host = serverConfig.host,
        ) {
            features.withEach {
                install()
            }

            routing {
                singlePageApplication {
                    react(serverConfig.reactDistPath)
                }

                unauthenticatedRoutes.withEach {
                    install()
                }

                authenticate("jwt") {
                    get("/test") {
                        val principal = call.principal<JWTPrincipal>()
                        principal?.getClaim("id", Long::class)?.let { id ->
                            call.respond("Hello, $id!")
                        } ?: call.respond("Hello, world!")
                    }
                }
            }
            configureSockets()
        }.also { server ->
            lifecycleModule.shutdownHandler.get.addHandler {
                server.stop(
                    gracePeriodMillis = serverConfig.gracefulShutdownTimeout.inWholeMilliseconds,
                    timeoutMillis = 5000,
                )
            }
        }
    }

    open val serverConfig by bean<ServerConfig> {
        Hocon.decodeFromConfig(configModule.config.get.getConfig("server"))
    }

    @Serializable
    data class ServerConfig(
        val port: Int,
        val host: String,
        val gracefulShutdownTimeout: Duration,
        val reactDistPath: String,
    )
}
