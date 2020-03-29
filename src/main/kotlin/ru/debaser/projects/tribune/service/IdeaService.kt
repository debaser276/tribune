package ru.debaser.projects.tribune.service

import ru.debaser.projects.tribune.exception.DatabaseException
import ru.debaser.projects.tribune.exception.IdeaNotFoundException
import ru.debaser.projects.tribune.model.IdeaModel
import ru.debaser.projects.tribune.repository.IdeaRepository

class IdeaService (
    private val repo: IdeaRepository
) {
    suspend fun postIdea(idea: IdeaModel): Long =
        repo.postIdea(idea) ?: throw DatabaseException()

    suspend fun getById(id: Long): IdeaModel =
        repo.getById(id) ?: throw IdeaNotFoundException()
}