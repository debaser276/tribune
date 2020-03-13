package ru.debaser.projects.tribune.model

data class Vote(
    val id: Long,
    val authorId: Long,
    val created: Long,
    val isUp: Boolean
)

data class IdeaModel(
    val id: Long,
    val authorId: Long,
    val created: Long,
    val content: String,
    val imageRes: String,
    val link: String? = null,
    val isHater: Boolean = false,
    val isPromoter: Boolean = false,
    val votes: Set<Vote> = setOf()
)