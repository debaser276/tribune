package ru.debaser.projects.tribune.db.data.vote

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ru.debaser.projects.tribune.db.data.idea.Ideas
import ru.debaser.projects.tribune.db.data.user.Users

object Votes : Table() {
    val id: Column<Long> = long("id").autoIncrement().primaryKey()
    val authorId: Column<Long> = long("author_id").references(Users.id)
    val ideaId: Column<Long> = long("idea_id").references(Ideas.id)
    val created: Column<Long> = long("created")
    val isUp: Column<Boolean> = bool("is_up")
}