package ru.debaser.projects.tribune.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.debaser.projects.tribune.db.data.idea.Ideas
import ru.debaser.projects.tribune.db.dbQuery
import ru.debaser.projects.tribune.model.IdeaModel

interface IdeaRepository {
    suspend fun save(idea: IdeaModel): Long?
    suspend fun getById(id: Long): IdeaModel?
}

class IdeaRepositoryDb: IdeaRepository {

    override suspend fun save(idea: IdeaModel): Long? = dbQuery {
        Ideas.insert {
            it[authorId] = idea.authorId
            it[created] = idea.created
            it[content] = idea.content
            it[media] = idea.media
            it[link] = idea.link
            it[likes] = idea.likes
            it[dislikes] = idea.dislikes
        }[Ideas.id]
    }

    override suspend fun getById(id: Long): IdeaModel? = dbQuery {
        Ideas.select { Ideas.id eq id }.map { toIdeaModel(it) }.singleOrNull()
    }

    private fun toIdeaModel(row: ResultRow): IdeaModel =
        IdeaModel(
            id = row[Ideas.id],
            authorId = row[Ideas.authorId],
            created = row[Ideas.created],
            content = row[Ideas.content],
            media = row[Ideas.media],
            link = row[Ideas.link],
            likes = row[Ideas.likes],
            dislikes = row[Ideas.dislikes]
        )
}