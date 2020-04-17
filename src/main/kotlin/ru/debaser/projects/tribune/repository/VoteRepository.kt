package ru.debaser.projects.tribune.repository

import org.jetbrains.exposed.sql.*
import ru.debaser.projects.tribune.db.data.user.Users
import ru.debaser.projects.tribune.db.data.vote.Votes
import ru.debaser.projects.tribune.db.dbQuery
import ru.debaser.projects.tribune.dto.VoteResponseDto

interface VoteRepository {
    suspend fun addVote(authorId: Long, ideaId: Long, isUp: Boolean): Long?
    suspend fun getLikesCount(ideaId: Long): Int
    suspend fun getDislikesCount(ideaId: Long): Int
    suspend fun getAll(ideaId: Long): List<VoteResponseDto>
    suspend fun getAfter(ideaId: Long, id: Long): List<VoteResponseDto>
    suspend fun getAuthorVotesCount(authorId: Long, isUp: Boolean): Int
    suspend fun getTop(num: Int, isUp: Boolean): List<Long>
}

class VoteRepositoryDb: VoteRepository {
    override suspend fun addVote(authorId: Long, ideaId: Long, isUp: Boolean): Long? = dbQuery {
        Votes.insert {
            it[this.authorId] = authorId
            it[this.ideaId] = ideaId
            it[created] = System.currentTimeMillis() / 1000
            it[this.isUp] = isUp
        }[Votes.id]
    }

    override suspend fun getLikesCount(ideaId: Long): Int = dbQuery {
        Votes.select { (Votes.ideaId eq ideaId) and (Votes.isUp eq true) }.count()
    }

    override suspend fun getDislikesCount(ideaId: Long): Int = dbQuery {
        Votes.select { (Votes.ideaId eq ideaId) and (Votes.isUp eq false) }.count()
    }

    override suspend fun getAll(ideaId: Long): List<VoteResponseDto> = dbQuery {
        (Votes innerJoin Users).select { Votes.ideaId eq ideaId }
            .orderBy(Votes.created to SortOrder.DESC)
            .map { toVoteResponseDto(it) }
    }

    override suspend fun getAfter(ideaId: Long, id: Long): List<VoteResponseDto> = dbQuery {
        (Votes innerJoin Users).select { (Votes.ideaId eq ideaId) and (Votes.id greater id) }
            .orderBy(Votes.created to SortOrder.DESC)
            .map { toVoteResponseDto(it) }
    }

    override suspend fun getAuthorVotesCount(authorId: Long, isUp: Boolean): Int = dbQuery {
        Votes.select { (Votes.authorId eq authorId) and (Votes.isUp eq isUp) }.count()
    }

    override suspend fun getTop(num: Int, isUp: Boolean): List<Long> = dbQuery {
        Votes.slice(Votes.authorId).select { Votes.isUp eq isUp }
            .groupBy(Votes.authorId)
            .orderBy(Votes.authorId.count() to SortOrder.DESC)
            .limit(num).map { it[Votes.authorId] }
    }

    private fun toVoteResponseDto(row: ResultRow): VoteResponseDto =
        VoteResponseDto(
            id = row[Votes.id],
            authorId = row[Votes.authorId],
            author = row[Users.username],
            isHater = row[Users.isHater],
            isPromoter = row[Users.isPromoter],
            avatar = row[Users.avatar],
            ideaId = row[Votes.ideaId],
            created = row[Votes.created],
            isUp = row[Votes.isUp]
        )
}