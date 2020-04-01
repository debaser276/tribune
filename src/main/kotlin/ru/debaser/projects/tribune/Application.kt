package ru.debaser.projects.tribune

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.routing.Routing
import io.ktor.server.cio.EngineMain
import org.kodein.di.generic.instance
import org.kodein.di.ktor.KodeinFeature
import org.kodein.di.ktor.kodein
import ru.debaser.projects.tribune.kodein.KodeinBuilder
import ru.debaser.projects.tribune.route.RoutingV1
import ru.debaser.projects.tribune.service.JWTTokenService
import ru.debaser.projects.tribune.service.UserService
import ru.debaser.projects.tribune.statuspages.ErrorHandler

fun main(args : Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            serializeNulls()
        }
    }

    install(StatusPages) {
        ErrorHandler().setup(this)
    }

    install(KodeinFeature) {
        KodeinBuilder(environment).setup(this)
    }

    install(Authentication) {
        jwt {
            val jwtService by kodein().instance<JWTTokenService>()
            verifier(jwtService.verifier)
            val userService by kodein().instance<UserService>()

            validate {
                val id = it.payload.getClaim("id").asLong()
                userService.getById(id)
            }
        }
    }

    install(Routing) {
        val routingV1 by kodein().instance<RoutingV1>()
        routingV1.setup(this)
    }
}