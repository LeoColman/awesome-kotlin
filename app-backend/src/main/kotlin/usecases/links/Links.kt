package usecases.links

import di.bean
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ktor.KtorRoute

open class LinksModule {
    open val linkSource by bean {
        LinkSource().get()
    }

    open val route by bean {
        LinksRoute(
            links = linkSource.get,
        )
    }
}

class LinksRoute(
    private val links: List<CategoryV1>,
) : KtorRoute {
    override fun Routing.install() {
        get("/api/links") {
            call.respond(links)
        }
    }
}

class LinkSource {
    fun get(): List<CategoryV1> {
        val json = LinkSource::class.java
            .classLoader
            .getResource("links/links.json")
            .readText()
        return Json.decodeFromString(json)
    }
}

@Serializable
data class LinkV1(
    val name: String? = null,
    val href: String? = null,
    val desc: String? = null,
    val platforms: List<PlatformTypeV1> = emptyList(),
    val tags: List<String> = emptyList(),
    val star: Int? = null,
    val update: String? = null,
    val state: LinkStateV1,
)

@Serializable
enum class PlatformTypeV1 {
    ANDROID,
    COMMON,
    IOS,
    JS,
    JVM,
    NATIVE,
    WASM
}

@Serializable
enum class LinkStateV1 {
    AWESOME,
    UNSUPPORTED,
    ARCHIVED,
    DEFAULT,
}

@Serializable
data class SubcategoryV1(
    val name: String,
    val links: MutableList<LinkV1>
)

@Serializable
data class CategoryV1(
    val name: String,
    val subcategories: MutableList<SubcategoryV1>
)
