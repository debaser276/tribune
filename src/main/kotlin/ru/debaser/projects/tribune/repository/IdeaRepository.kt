package ru.debaser.projects.tribune.repository

import org.jetbrains.exposed.sql.insert
import ru.debaser.projects.tribune.db.data.idea.Ideas
import ru.debaser.projects.tribune.db.dbQuery
import ru.debaser.projects.tribune.model.IdeaModel

interface IdeaRepository {
    suspend fun save(idea: IdeaModel): Long?
}

class IdeaRepositoryDb: IdeaRepository {

    override suspend fun save(idea: IdeaModel): Long? = dbQuery {
        Ideas.insert {
            it[authorId] = idea.authorId
            it[created] = idea.created
            it[content] = idea.content
            it[media] = idea.media
            it[link] = idea.link
            it[votes] = idea.votes.joinToString(",")
        }[Ideas.id]
    }

}