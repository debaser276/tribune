package ru.debaser.projects.tribune.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

class DatabaseFactory {
    fun init() {
        Database.connect(hikari())
    }

    private fun hikari(): HikariDataSource {
        val hikariConfig = HikariConfig().apply {
            username = "debaser"
            password = "gjlyfchtv"
            jdbcUrl = "jdbc:postgresql://localhost/test"
            driverClassName = "org.postgresql.Driver"
        }
        return HikariDataSource(hikariConfig)
    }
}