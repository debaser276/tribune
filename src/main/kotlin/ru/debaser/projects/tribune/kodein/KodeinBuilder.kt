package ru.debaser.projects.tribune.kodein

import io.ktor.application.ApplicationEnvironment
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.with
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import ru.debaser.projects.tribune.db.DatabaseFactory
import ru.debaser.projects.tribune.repository.UserRepository
import ru.debaser.projects.tribune.repository.UserRepositoryDb
import ru.debaser.projects.tribune.route.RoutingV1
import ru.debaser.projects.tribune.service.JWTTokenService
import ru.debaser.projects.tribune.service.UserService
import javax.naming.ConfigurationException

class KodeinBuilder(private val environment: ApplicationEnvironment) {

    fun setup(builder: Kodein.Builder) {
        with (builder) {
            constant(tag = "jwt-secret") with (
                    environment.config.propertyOrNull("tribune.jwt.secret")?.getString() ?:
                    throw ConfigurationException("JWT secret is not specified"))
            constant(tag = "jdbc-url") with (
                    environment.config.propertyOrNull("tribune.db.jdbcUrl")?.getString() ?:
                    throw ConfigurationException("Jdbc url is not specified"))
            bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
            bind<DatabaseFactory>() with eagerSingleton { DatabaseFactory(instance(tag = "jdbc-url")).apply { init() } }
            bind<RoutingV1>() with eagerSingleton { RoutingV1(instance()) }
            bind<UserService>() with eagerSingleton { UserService(instance(), instance(), instance()) }
            bind<UserRepository>() with eagerSingleton { UserRepositoryDb() }
            bind<JWTTokenService>() with eagerSingleton {
                JWTTokenService(
                    instance(tag = "jwt-secret")
                )
            }
        }
    }
}