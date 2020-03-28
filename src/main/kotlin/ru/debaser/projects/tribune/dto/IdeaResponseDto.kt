package ru.debaser.projects.tribune.dto

import ru.debaser.projects.tribune.model.IdeaModel

data class IdeaResponseDto(
    val id: Long,
    val authorId: Long,
    val created: Long,
    val content: String,
    val media: String,
    val link: String?,
    val likes: Int,
    val dislikes: Int
) {
    companion object {
        fun fromModel(idea: IdeaModel) = IdeaResponseDto(
            id = idea.id,
            authorId = idea.authorId,
            created = idea.created,
            content = idea.content,
            media = idea.media,
            link = idea.link,
            likes = idea.likes,
            dislikes = idea.dislikes
        )
    }
}