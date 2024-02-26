package ktor.features

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import ktor.KtorFeature
import usecases.signup.JwtModule
import utils.logger

class AuthenticationFeature(
    private val jwtConfig: JwtModule.JwtConfig,
) : KtorFeature {
    override fun Application.install() {
        authentication {
            jwt("jwt") {
                realm = jwtConfig.realm
                authHeader { call ->
                    try {
                        call.request.cookies["token"]
                            ?.let { cookieValue ->
                                parseAuthorizationHeader("Bearer $cookieValue")
                            }
                    } catch (cause: IllegalArgumentException) {
                        logger {}.info("Failed to parse JWT from cookie", cause)
                        null
                    }
                }
                verifier(
                    JWT
                        .require(Algorithm.HMAC512(jwtConfig.secret))
                        .withAudience(jwtConfig.audience)
                        .withIssuer(jwtConfig.issuer)
                        .build()
                )
                validate { credential ->
                    if (credential.payload.audience.contains(jwtConfig.audience)) {
                        JWTPrincipal(credential.payload)
                    } else {
                        null
                    }
                }
            }
        }
    }
}
