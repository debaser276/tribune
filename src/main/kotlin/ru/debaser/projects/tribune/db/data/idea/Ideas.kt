package ru.debaser.projects.tribune.db.data.idea

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import ru.debaser.projects.tribune.db.data.user.Users

object Ideas : Table() {
    val id: Column<Long> = long("id").autoIncrement().primaryKey()
    val authorId: Column<Long> = long("author_id").references(Users.id)
    val created: Column<Long> = long("created")
    val content: Column<String> = text("content")
    val media: Column<String> = text("media")
    val link: Column<String?> = text("link").nullable()
    val likes: Column<String> = text("likes").default("")
    val dislikes: Column<String> = text("dislikes").default("")
}