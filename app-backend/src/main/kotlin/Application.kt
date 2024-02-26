@file:JvmName("Application")

import config.ConfigModule
import di.bean
import ktor.KtorFeaturesModule
import lifecycle.LifecycleModule
import metrics.MetricsModule
import usecases.github.GithubModule
import usecases.kug.KugModule
import usecases.links.LinksModule
import usecases.healthcheck.HealthcheckModule
import usecases.rss.RssModule
import usecases.signup.JwtModule
import usecases.signup.LoginModule
import usecases.signup.RegisterModule
import usecases.version.KotlinVersionModule
import utils.close
import utils.logger
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import kotlin.time.Duration.Companion.milliseconds

suspend fun main() {
    ApplicationFactory().use {
        it.run()
    }
}

open class ApplicationFactory : AutoCloseable {
    open val httpClientModule by lazy {
        HttpClientModule()
    }

    open val yamlModule by lazy {
        YamlModule()
    }

    open val xmlModule by lazy {
        XmlModule()
    }

    open val configModule by lazy {
        ConfigModule()
    }

    open val jdbcModule by lazy {
        JdbcModule(
            configModule = configModule,
        )
    }

    open val lifecycleModule by lazy {
        LifecycleModule()
    }

    open val jwtModule by lazy {
        JwtModule(
            configModule = configModule,
        )
    }

    open val jooqModule by lazy {
        JooqModule(
            jdbcModule = jdbcModule,
        )
    }

    open val flywayModule by lazy {
        FlywayModule(
            jdbcModule = jdbcModule,
        )
    }

    open val loginModule by lazy {
        LoginModule(
            jooqModule = jooqModule,
            jwtModule = jwtModule,
        )
    }

    open val registerModule by lazy {
        RegisterModule(
            jooqModule = jooqModule,
        )
    }

    open val kugModule by lazy {
        KugModule(
            httpClientModule = httpClientModule,
            yamlModule = yamlModule,
            jooqModule = jooqModule,
        )
    }

    open val githubModule by lazy {
        GithubModule(
            configModule = configModule,
            httpClientModule = httpClientModule,
        )
    }

    open val healthcheckModule by lazy {
        HealthcheckModule()
    }

    open val linksModule by lazy {
        LinksModule()
    }

    open val metricsModule by lazy {
        MetricsModule(
            jdbcModule = jdbcModule,
        )
    }

    open val rssModule by lazy {
        RssModule()
    }

    open val kotlinVersionModule by lazy {
        KotlinVersionModule(
            xmlModule = xmlModule,
            httpClientModule = httpClientModule,
        )
    }

    open val ktorFeaturesModule by lazy {
        KtorFeaturesModule(
            jwtModule = jwtModule,
            metricsModule = metricsModule,
        )
    }

    open val serverModule by lazy {
        ServerModule(
            githubModule = githubModule,
            healthcheckModule = healthcheckModule,
            loginModule = loginModule,
            registerModule = registerModule,
            linksModule = linksModule,
            kugModule = kugModule,
            metricsModule = metricsModule,
            lifecycleModule = lifecycleModule,
            configModule = configModule,
            rssModule = rssModule,
            kotlinVersionModule = kotlinVersionModule,
            ktorFeaturesModule = ktorFeaturesModule,
        )
    }

    open suspend fun run() {
        val gracefulShutdown = lifecycleModule.gracefulShutdown.get
        lifecycleModule.shutdownHandler.get.registerHook()
        flywayModule.flyway.get.migrate()
        serverModule.ktorServer.get.start(wait = false)
        log.get.info("Server started in {}", runtimeMXBean.get.uptime.milliseconds)
        gracefulShutdown.waitForShutdown()
    }

    open val runtimeMXBean by bean<RuntimeMXBean> {
        ManagementFactory.getRuntimeMXBean()
    }

    open val log by bean {
        logger<ApplicationFactory>()
    }

    override fun close() {
        jdbcModule.close {}
        httpClientModule.close {}
    }
}
