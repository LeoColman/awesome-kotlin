package ktor.features

import io.ktor.server.application.*
import io.ktor.server.locations.*
import ktor.KtorFeature

class LocationsFeature : KtorFeature {
    override fun Application.install() {
        install(Locations)
    }
}
