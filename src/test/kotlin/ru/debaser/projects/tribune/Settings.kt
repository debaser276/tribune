package ru.debaser.projects.tribune

import io.ktor.application.Application
import io.ktor.config.MapApplicationConfig
import io.ktor.http.ContentType
import io.ktor.http.withCharset
import org.testcontainers.containers.PostgreSQLContainer
import java.nio.file.Files

class Settings {
    companion object {
        class AppPostgreSQLContainer : PostgreSQLContainer<AppPostgreSQLContainer>("postgres:latest")
        val jsonContentType = ContentType.Application.Json.withCharset(Charsets.UTF_8)
        private val uploadPath = Files.createTempDirectory("test").toString()
        private val postgresContainer = AppPostgreSQLContainer().apply {
            start()
        }
        val configure: Application.() -> Unit = {
            (environment.config as MapApplicationConfig).apply {
                put("tribune.upload.dir", uploadPath)
                put("tribune.jwt.secret", "2875f2518dd74feeb3260ebe1d24cb09")
//                put("tribune.db.jdbcUrl", "postgres://debaser:password@localhost:54321/test")
                put("tribune.db.jdbcUrl", "postgres://${postgresContainer.username}:${postgresContainer.password}@${postgresContainer.containerIpAddress}:${postgresContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)}/${postgresContainer.databaseName}")
                put("tribune.settings.reader-dislikes", "1")
                put("tribune.settings.result-size", "20")
            }
            module()
        }
    }
}