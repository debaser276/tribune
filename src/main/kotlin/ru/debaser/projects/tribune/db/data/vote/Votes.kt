package ru.debaser.projects.tribune.db.data.vote

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

class Votes : Table() {
    val id: Column<Long> = long("id").autoIncrement().primaryKey()
    val authorId: Column<Long> = long("author_id")
    val ideaId: Column<Long> = long("idea_id")
    val created: Column<Long> = long("created")
    val isUp: Column<Boolean> = bool("is_up")
}