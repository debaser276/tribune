package ru.debaser.projects.tribune.db.data.idea

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Ideas : Table() {
    val id: Column<Long> = long("id").autoIncrement().primaryKey()
    val authorId: Column<Long> = long("author_id")
    val created: Column<Long> = long("created")
    val content: Column<String> = text("content")
    val media: Column<String> = text("media")
    val link: Column<String?> = text("link").nullable()
    val votes: Column<String> = text("votes")
    val likes: Column<Int> = integer("likes")
    val dislikes: Column<Int> = integer("dislikes")
}