package ru.debaser.projects.tribune.kodein

import io.ktor.application.Application
import io.ktor.application.ApplicationEnvironment
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.with
import ru.debaser.projects.tribune.db.DatabaseFactory
import ru.debaser.projects.tribune.repository.UserRepository
import ru.debaser.projects.tribune.repository.UserRepositoryInMemoryWithMutex
import ru.debaser.projects.tribune.route.RoutingV1
import ru.debaser.projects.tribune.service.JWTTokenService
import ru.debaser.projects.tribune.service.UserService
import javax.naming.ConfigurationException

class KodeinBuilder(private val environment: ApplicationEnvironment) {

    fun setup(builder: Kodein.Builder) {
        with (builder) {
            constant(tag = "jwt-secret") with (
                    environment.config.propertyOrNull("tribune.jwt.secret")?.getString() ?:
                    throw ConfigurationException("JWT secret is not specified")
                    )
            bind<DatabaseFactory>() with eagerSingleton { DatabaseFactory().apply { init() } }
            bind<RoutingV1>() with eagerSingleton { RoutingV1(instance()) }
            bind<UserService>() with eagerSingleton { UserService(instance(), instance()) }
            bind<UserRepository>() with eagerSingleton { UserRepositoryInMemoryWithMutex() }
            bind<JWTTokenService>() with eagerSingleton {
                JWTTokenService(
                    instance(tag = "jwt-secret")
                )
            }
        }
    }
}