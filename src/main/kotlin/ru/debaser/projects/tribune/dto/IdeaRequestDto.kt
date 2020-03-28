package ru.debaser.projects.tribune.dto

data class IdeaRequestDto(
    val authorId: Long,
    val content: String,
    val media: String
)