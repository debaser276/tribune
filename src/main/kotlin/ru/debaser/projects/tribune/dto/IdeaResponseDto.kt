package ru.debaser.projects.tribune.dto

data class IdeaResponseDto(
    val id: Long,
    val authorId: Long,
    val author: String,
    val isHater: Boolean,
    val isPromoter: Boolean,
    val created: Long,
    val content: String,
    val media: String,
    val link: String?,
    val likes: Set<Long>,
    val dislikes: Set<Long>
)