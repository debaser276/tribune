package ru.debaser.projects.tribune.db.data.user

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id: Column<Long> = long("id").autoIncrement().primaryKey()
    val username: Column<String> = varchar("username", 100)
    val password: Column<String> = varchar("password", 100)
    val likes: Column<Int> = integer("likes")
    val notLikes: Column<Int> = integer("notLikes")
    val isHAter: Column<Boolean> = bool("isHater")
    val isPromoter: Column<Boolean> = bool("isPromoter")
}