package ru.debaser.projects.tribune.repository

import org.jetbrains.exposed.sql.*
import ru.debaser.projects.tribune.db.data.idea.Ideas
import ru.debaser.projects.tribune.db.dbQuery
import ru.debaser.projects.tribune.model.IdeaModel

interface IdeaRepository {
    suspend fun postIdea(idea: IdeaModel): Long?
    suspend fun getById(id: Long): IdeaModel?
    suspend fun like(ideaId: Long, userId: Long)
    suspend fun dislike(ideaId: Long, userId: Long)
    suspend fun getAll(): List<IdeaModel>
    suspend fun getBefore(id: Long): List<IdeaModel>
    suspend fun getAfter(id: Long): List<IdeaModel>
    suspend fun getRecentByAuthor(authorId: Long): List<IdeaModel>
    suspend fun getBeforeByAuthor(authorId: Long, id: Long): List<IdeaModel>
    suspend fun getAfterByAuthor(authorId: Long, id: Long): List<IdeaModel>
}

class IdeaRepositoryDb: IdeaRepository {

    override suspend fun postIdea(idea: IdeaModel): Long? = dbQuery {
        Ideas.insert {
            it[authorId] = idea.authorId
            it[created] = System.currentTimeMillis() / 1000
            it[content] = idea.content
            it[media] = idea.media
            it[link] = idea.link
        }[Ideas.id]
    }

    override suspend fun getById(id: Long): IdeaModel? = dbQuery {
        Ideas.select { Ideas.id eq id }.map { toIdeaModel(it) }.singleOrNull()
    }

    override suspend fun like(ideaId: Long, userId: Long) {
        dbQuery {
            val likes = Ideas.select { Ideas.id eq ideaId }.map { toIdeaModel(it) }.single().likes.toMutableSet()
            likes.add(userId)
            Ideas.update({ Ideas.id eq ideaId }) {
                it[Ideas.likes] = likes.toSet().joinToString(",")
            }
        }
    }

    override suspend fun dislike(ideaId: Long, userId: Long) {
        dbQuery {
            val dislikes = Ideas.select { Ideas.id eq ideaId }.map { toIdeaModel(it) }.single().dislikes.toMutableSet()
            dislikes.add(userId)
            Ideas.update({ Ideas.id eq ideaId }) {
                it[Ideas.dislikes] = dislikes.toSet().joinToString(",")
            }
        }
    }

    override suspend fun getAll(): List<IdeaModel> = dbQuery {
        Ideas.selectAll()
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaModel(it) } }

    override suspend fun getBefore(id: Long): List<IdeaModel> = dbQuery {
        Ideas.select { Ideas.id less id }
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaModel(it) } }

    override suspend fun getAfter(id: Long): List<IdeaModel> = dbQuery {
        Ideas.select { Ideas.id greater id }
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaModel(it) } }

    override suspend fun getRecentByAuthor(authorId: Long): List<IdeaModel> = dbQuery {
        Ideas.select { Ideas.authorId eq authorId }
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaModel(it) } }

    override suspend fun getBeforeByAuthor(authorId: Long, id: Long): List<IdeaModel> = dbQuery {
        Ideas.select { (Ideas.authorId eq authorId) and (Ideas.id less id) }
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaModel(it) } }

    override suspend fun getAfterByAuthor(authorId: Long, id: Long): List<IdeaModel> = dbQuery {
        Ideas.select { (Ideas.authorId eq authorId) and (Ideas.id greater id) }
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaModel(it) } }

    private fun toIdeaModel(row: ResultRow): IdeaModel =
        IdeaModel(
            id = row[Ideas.id],
            authorId = row[Ideas.authorId],
            created = row[Ideas.created],
            content = row[Ideas.content],
            media = row[Ideas.media],
            link = row[Ideas.link],
            likes = splitOrEmptySet(row, Ideas.likes),
            dislikes = splitOrEmptySet(row, Ideas.dislikes)
        )

    private fun splitOrEmptySet(row: ResultRow, col: Column<String>) =
        if (row[col].isNotEmpty()) row[col].split(",").map { it.toLong() }.toSet() else setOf()
}