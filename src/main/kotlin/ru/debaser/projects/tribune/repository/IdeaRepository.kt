package ru.debaser.projects.tribune.repository

import org.jetbrains.exposed.sql.*
import ru.debaser.projects.tribune.db.data.idea.Ideas
import ru.debaser.projects.tribune.db.data.user.Users
import ru.debaser.projects.tribune.db.dbQuery
import ru.debaser.projects.tribune.dto.IdeaRequestDto
import ru.debaser.projects.tribune.dto.IdeaResponseDto
import ru.debaser.projects.tribune.model.IdeaModel

interface IdeaRepository {
    suspend fun postIdea(userId: Long, ideaRequestDto: IdeaRequestDto): Long?
    suspend fun getById(id: Long): IdeaResponseDto?
    suspend fun like(ideaId: Long, userId: Long)
    suspend fun dislike(ideaId: Long, userId: Long)
    suspend fun getAll(): List<IdeaResponseDto>
    suspend fun getBefore(id: Long): List<IdeaResponseDto>
    suspend fun getAfter(id: Long): List<IdeaResponseDto>
    suspend fun getRecentByAuthor(authorId: Long): List<IdeaResponseDto>
    suspend fun getBeforeByAuthor(authorId: Long, id: Long): List<IdeaResponseDto>
    suspend fun getAfterByAuthor(authorId: Long, id: Long): List<IdeaResponseDto>
}

class IdeaRepositoryDb: IdeaRepository {

    override suspend fun postIdea(userId: Long, ideaRequestDto: IdeaRequestDto): Long? = dbQuery {
        Ideas.insert {
            it[authorId] = userId
            it[created] = System.currentTimeMillis() / 1000
            it[content] = ideaRequestDto.content
            it[media] = ideaRequestDto.media
            it[link] = ideaRequestDto.link
        }[Ideas.id]
    }

    override suspend fun getById(id: Long): IdeaResponseDto? = dbQuery {
        (Ideas innerJoin Users).select { Ideas.id eq id }.map { toIdeaResponseDto(it) }.singleOrNull()
    }

    override suspend fun like(ideaId: Long, userId: Long) {
        dbQuery {
            val likes =
                (Ideas innerJoin Users).select { Ideas.id eq ideaId }
                    .map { toIdeaResponseDto(it) }
                    .single().likes.toMutableSet()
            likes.add(userId)
            Ideas.update({ Ideas.id eq ideaId }) {
                it[Ideas.likes] = likes.toSet().joinToString(",")
            }
        }
    }

    override suspend fun dislike(ideaId: Long, userId: Long) {
        dbQuery {
            val dislikes = (Ideas innerJoin Users).select { Ideas.id eq ideaId }
                .map { toIdeaResponseDto(it) }.single().dislikes.toMutableSet()
            dislikes.add(userId)
            Ideas.update({ Ideas.id eq ideaId }) {
                it[Ideas.dislikes] = dislikes.toSet().joinToString(",")
            }
        }
    }

    override suspend fun getAll(): List<IdeaResponseDto> = dbQuery {
        (Ideas innerJoin Users).selectAll()
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaResponseDto(it) } }

    override suspend fun getBefore(id: Long): List<IdeaResponseDto> = dbQuery {
        (Ideas innerJoin Users).select { Ideas.id less id }
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaResponseDto(it) } }

    override suspend fun getAfter(id: Long): List<IdeaResponseDto> = dbQuery {
        (Ideas innerJoin Users).select { Ideas.id greater id }
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaResponseDto(it) } }

    override suspend fun getRecentByAuthor(authorId: Long): List<IdeaResponseDto> = dbQuery {
        (Ideas innerJoin Users).select { Ideas.authorId eq authorId }
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaResponseDto(it) } }

    override suspend fun getBeforeByAuthor(authorId: Long, id: Long): List<IdeaResponseDto> = dbQuery {
        (Ideas innerJoin Users).select { (Ideas.authorId eq authorId) and (Ideas.id less id) }
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaResponseDto(it) } }

    override suspend fun getAfterByAuthor(authorId: Long, id: Long): List<IdeaResponseDto> = dbQuery {
        (Ideas innerJoin Users).select { (Ideas.authorId eq authorId) and (Ideas.id greater id) }
            .orderBy(Ideas.id to SortOrder.DESC)
            .map { toIdeaResponseDto(it) } }

    private fun toIdeaResponseDto(row: ResultRow): IdeaResponseDto =
        IdeaResponseDto(
            id = row[Ideas.id],
            authorId = row[Ideas.authorId],
            author = row[Users.username],
            isHater = row[Users.isHater],
            isPromoter = row[Users.isPromoter],
            avatar = row[Users.avatar],
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