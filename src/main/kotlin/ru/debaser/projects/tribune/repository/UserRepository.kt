package ru.debaser.projects.tribune.repository

import org.jetbrains.exposed.sql.*
import ru.debaser.projects.tribune.db.data.user.Users
import ru.debaser.projects.tribune.db.dbQuery
import ru.debaser.projects.tribune.model.UserModel

interface UserRepository {
    suspend fun save(user: UserModel): Long?
    suspend fun getById(id: Long): UserModel?
    suspend fun getByUsername(username: String): UserModel?
    suspend fun like(id: Long): Int
}

class UserRepositoryDb: UserRepository {

    override suspend fun save(item: UserModel): Long? = dbQuery {
        Users.insert {
            it[username] = item.username
            it[password] = item.password
            it[likes] = item.likes
            it[dislikes] = item.dislikes
            it[isReader] = item.isReader
        }[Users.id]
    }

    override suspend fun getById(id: Long): UserModel? = dbQuery {
        Users.select { Users.id eq id }.map { toUserModel(it) }.singleOrNull()
    }
    override suspend fun getByUsername(username: String): UserModel? = dbQuery {
        Users.select { Users.username eq username }.map { toUserModel(it) }.singleOrNull()
    }

    override suspend fun like(id: Long) = dbQuery {
        Users.update({ Users.id eq id }) {
            with(SqlExpressionBuilder) {
                it.update(likes, likes + 1)
            }
        }
    }


    private fun toUserModel(row: ResultRow): UserModel =
        UserModel(
            id = row[Users.id],
            username = row[Users.username],
            password = row[Users.password],
            likes = row[Users.likes],
            dislikes = row[Users.dislikes],
            isReader = row[Users.isReader]
        )
}