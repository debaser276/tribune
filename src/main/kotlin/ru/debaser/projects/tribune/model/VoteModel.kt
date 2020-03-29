package ru.debaser.projects.tribune.model

data class VoteModel(
    val id: Long,
    val authorId: Long,
    val ideaId: Long,
    val created: Long,
    val isUp: Boolean
)