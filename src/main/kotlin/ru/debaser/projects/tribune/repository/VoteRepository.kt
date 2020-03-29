package ru.debaser.projects.tribune.repository

import org.jetbrains.exposed.sql.insert
import ru.debaser.projects.tribune.db.data.vote.Votes
import ru.debaser.projects.tribune.db.dbQuery
import ru.debaser.projects.tribune.model.VoteModel

interface VoteRepository {
    suspend fun addVote(authorId: Long, ideaId: Long, isUp: Boolean): Long?
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
}