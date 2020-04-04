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
    private val readerDislikes: Int,
    private val resultSize: Int
) {

    private val mutex = Mutex()

    suspend fun getById(id: Long): IdeaResponseDto =
        ideaRepo.getById(id) ?: throw IdeaNotFoundException()

    suspend fun postIdea(idea: IdeaModel): Long =
        ideaRepo.postIdea(idea) ?: throw DatabaseException()

    suspend fun like(ideaId: Long, userId: Long): IdeaResponseDto {
        mutex.withLock {
            ideaRepo.like(ideaId, userId)
            voteRepo.addVote(userId, ideaId, true)
            return getById(ideaId)
        }
    }

    suspend fun dislike(ideaId: Long, userId: Long): IdeaResponseDto {
        mutex.withLock {
            ideaRepo.dislike(ideaId, userId)
            voteRepo.addVote(userId, ideaId, false)
            return getById(ideaId)
        }
    }

    suspend fun isReaderEnough(id: Long): Boolean {
        val likes = voteRepo.getLikesCount(id)
        val dislikes = voteRepo.getDislikesCount(id)
        return likes < 1 && dislikes > readerDislikes
    }

    suspend fun getRecent(): List<IdeaResponseDto> =
        ideaRepo.getAll().take(resultSize)

    suspend fun getBefore(id: Long): List<IdeaResponseDto> =
        ideaRepo.getBefore(id).take(resultSize)

    suspend fun getAfter(id: Long): List<IdeaResponseDto> =
        ideaRepo.getAfter(id).take(resultSize)

    suspend fun getRecentByAuthor(authorId: Long): List<IdeaResponseDto> =
        ideaRepo.getRecentByAuthor(authorId).take(resultSize)

    suspend fun getBeforeByAuthor(authorId: Long, id: Long): List<IdeaResponseDto> =
        ideaRepo.getBeforeByAuthor(authorId, id).take(resultSize)

    suspend fun getAfterByAuthor(authorId: Long, id: Long): List<IdeaResponseDto> =
        ideaRepo.getAfterByAuthor(authorId, id).take(resultSize)

    suspend fun getAllVotes(ideaId: Long): List<VoteModel> =
        voteRepo.getAll(ideaId)

    suspend fun getAfterVotes(ideaId: Long, voteId: Long): List<VoteModel> =
        voteRepo.getAfter(ideaId, voteId).take(resultSize).toList()
}