package ru.debaser.projects.tribune.service

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.debaser.projects.tribune.dto.IdeaResponseDto
import ru.debaser.projects.tribune.exception.DatabaseException
import ru.debaser.projects.tribune.exception.IdeaNotFoundException
import ru.debaser.projects.tribune.model.IdeaModel
import ru.debaser.projects.tribune.model.VoteModel
import ru.debaser.projects.tribune.repository.IdeaRepository
import ru.debaser.projects.tribune.repository.VoteRepository

class IdeaService (
    private val ideaRepo: IdeaRepository,
    private val voteRepo: VoteRepository,
    private val readerDislikesStr: String
) {
    private val mutex = Mutex()
    private val readerDislikes = readerDislikesStr.toInt()

    suspend fun postIdea(idea: IdeaModel): Long =
        ideaRepo.postIdea(idea) ?: throw DatabaseException()

    suspend fun getById(id: Long): IdeaModel =
        ideaRepo.getById(id) ?: throw IdeaNotFoundException()

    suspend fun like(ideaId: Long, userId: Long): IdeaResponseDto {
        mutex.withLock {
            ideaRepo.like(ideaId, userId)
            voteRepo.addVote(userId, ideaId, true)
            return IdeaResponseDto.fromModel(getById(ideaId))
        }
    }

    suspend fun dislike(ideaId: Long, userId: Long): IdeaResponseDto {
        mutex.withLock {
            ideaRepo.dislike(ideaId, userId)
            voteRepo.addVote(userId, ideaId, false)
            return IdeaResponseDto.fromModel(getById(ideaId))
        }
    }

    suspend fun isReaderEnough(id: Long): Boolean {
        val likes = voteRepo.getLikesCount(id)
        val dislikes = voteRepo.getDislikesCount(id)
        return likes < 1 && dislikes > readerDislikes
    }
}