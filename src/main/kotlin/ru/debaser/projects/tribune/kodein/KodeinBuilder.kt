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
import ru.debaser.projects.tribune.repository.*
import ru.debaser.projects.tribune.route.RoutingV1
import ru.debaser.projects.tribune.service.*
import javax.naming.ConfigurationException

class KodeinBuilder(private val environment: ApplicationEnvironment) {

    fun setup(builder: Kodein.Builder) {
        with (builder) {
            constant(tag = "jwt-secret") with (
                    environment.config.propertyOrNull("tribune.jwt.secret")?.getString() ?:
                    throw ConfigurationException("JWT secret is not specified"))
            constant(tag = "jdbc-url") with (
                    environment.config.propertyOrNull("tribune.db.jdbcUrl")?.getString() ?:
                    throw ConfigurationException("JdbcUrl is not specified"))
            constant(tag = "upload-dir") with (
                    environment.config.propertyOrNull("tribune.upload.dir")?.getString() ?:
                    throw ConfigurationException("Upload dir is not specified"))
            constant(tag = "reader-dislikes") with (
                    environment.config.propertyOrNull("tribune.settings.reader-dislikes")?.getString()?.toInt() ?:
                    throw ConfigurationException("Reader-dislikes is not specified"))
            constant(tag = "result-size") with (
                    environment.config.propertyOrNull("tribune.settings.result-size")?.getString()?.toInt() ?:
                    throw ConfigurationException("Result-size is not specified"))
            constant(tag = "cloud-name") with (
                    environment.config.propertyOrNull("tribune.cloudinary.cloud-name")?.getString() ?:
                    throw ConfigurationException("Cloud-name is not specified"))
            constant(tag = "api-key") with (
                    environment.config.propertyOrNull("tribune.cloudinary.api-key")?.getString() ?:
                    throw ConfigurationException("Api-key is not specified"))
            constant(tag = "api-secret") with (
                    environment.config.propertyOrNull("tribune.cloudinary.api-secret")?.getString() ?:
                    throw ConfigurationException("Api-secret is not specified"))
            constant(tag = "top-badge") with (
                    environment.config.propertyOrNull("tribune.settings.top-badge")?.getString()?.toInt() ?:
                    throw ConfigurationException("Top-badge is not specified"))
            constant(tag = "fcm-password") with (
                    environment.config.propertyOrNull("tribune.fcm.password")?.getString() ?:
                    throw ConfigurationException("Fcm-password is not specified"))
            constant(tag = "fcm-salt") with (
                    environment.config.propertyOrNull("tribune.fcm.salt")?.getString() ?:
                    throw ConfigurationException("Fcm-salt is not specified"))
            constant(tag = "fcm-dbUrl") with (
                    environment.config.propertyOrNull("tribune.fcm.dbUrl")?.getString() ?:
                    throw ConfigurationException("Fcm-dbUrl is not specified"))
            constant(tag = "fcm-path") with (
                    environment.config.propertyOrNull("tribune.fcm.path")?.getString() ?:
                    throw ConfigurationException("Fcm-path is not specified"))
            bind<FileService>() with eagerSingleton {
                FileService(
                    instance(tag = "upload-dir"),
                    instance(tag = "cloud-name"),
                    instance(tag = "api-key"),
                    instance(tag = "api-secret")
                ) }
            bind<DatabaseFactory>() with eagerSingleton { DatabaseFactory(instance(tag = "jdbc-url")).apply { init() } }
            bind<RoutingV1>() with eagerSingleton {
                RoutingV1(
                    instance(tag = "upload-dir"),
                    instance(),
                    instance(),
                    instance(),
                    instance(),
                    environment.log
                ) }
            bind<UserService>() with eagerSingleton { UserService(instance(), instance(), instance()) }
            bind<UserRepository>() with eagerSingleton { UserRepositoryDb() }
            bind<JWTTokenService>() with eagerSingleton {
                JWTTokenService(
                    instance(tag = "jwt-secret")
                )
            }
            bind<PasswordEncoder>() with eagerSingleton { BCryptPasswordEncoder() }
            bind<IdeaRepository>() with eagerSingleton { IdeaRepositoryDb() }
            bind<VoteRepository>() with eagerSingleton { VoteRepositoryDb() }
            bind<IdeaService>() with eagerSingleton {
                IdeaService(
                    instance(),
                    instance(),
                    instance(tag = "reader-dislikes"),
                    instance(tag = "result-size"),
                    instance(tag = "top-badge")
                )
            }
            bind<FCMService>() with eagerSingleton {
                FCMService(
                    instance(tag = "fcm-dbUrl"),
                    instance(tag = "fcm-password"),
                    instance(tag = "fcm-salt"),
                    instance(tag = "fcm-path")
                )
            }
        }
    }
}