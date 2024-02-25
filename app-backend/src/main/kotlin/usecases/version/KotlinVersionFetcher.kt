package usecases.version

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import java.util.concurrent.ConcurrentHashMap

/**
 * Fetch latest kotlin versions from maven central.
 *
 * @author Ibragimov Ruslan
 */
interface KotlinVersionFetcher {
    suspend fun getLatestVersions(branches: List<String>): List<String>
}

class CachedVersionFetcher(
    private val kotlinVersionFetcher: KotlinVersionFetcher,
) : KotlinVersionFetcher {
    private val cache = ConcurrentHashMap<List<String>, List<String>>(1)

    override suspend fun getLatestVersions(branches: List<String>): List<String> {
        return cache.getOrPut(branches) {
            kotlinVersionFetcher.getLatestVersions(branches)
        }
    }
}

class MavenCentralKotlinVersionFetcher(
    private val xmlMapper: XmlMapper,
    private val httpClient: HttpClient,
) : KotlinVersionFetcher {
    override suspend fun getLatestVersions(
        branches: List<String>,
    ): List<String> {
        val url = "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/maven-metadata.xml"

        val xml = httpClient.get(url).bodyAsText()

        val metadata = xmlMapper.readValue(xml, MavenMetadata::class.java)
        val versions = metadata.versioning.versions

        return branches.map { findMax(versions, it) }
    }

    private fun findMax(versions: List<String>, version: String): String {
        return versions
            .filter { it.matches(versionRegex) }
            .filter { it.startsWith(version) }
            .maxOrNull() ?: ""
    }

    private val versionRegex = Regex("^\\d+.\\d+.\\d+$")
}

@JsonIgnoreProperties("groupId", "artifactId")
data class MavenMetadata(
    val versioning: MavenVersioning
)

@JsonIgnoreProperties("latest", "release", "lastUpdated")
data class MavenVersioning(
    val versions: List<String>
)
