package ru.debaser.projects.tribune.dto

data class VoteResponseDto(
    val id: Long,
    val authorId: Long,
    val isHater: Boolean,
    val isPromoter: Boolean,
    val avatar: String,
    val ideaId: Long,
    val created: Long,
    val isUp: Boolean
)