package ru.debaser.projects.tribune.repository

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.debaser.projects.tribune.db.data.user.Users
import ru.debaser.projects.tribune.db.dbQuery
import ru.debaser.projects.tribune.model.UserModel

interface UserRepository {
    suspend fun save(user: UserModel): Long?
    suspend fun getByUsername(username: String): UserModel?
}

class UserRepositoryInMemoryWithMutex: UserRepository {

    override suspend fun save(item: UserModel): Long? = dbQuery {
        Users.insert {
            it[username] = item.username
            it[password] = item.password
        }[Users.id]
    }

    override suspend fun getByUsername(username: String): UserModel? = dbQuery {
        Users.select { Users.username eq username }.map { toUserModel(it) }.singleOrNull()
    }

    private fun toUserModel(row: ResultRow): UserModel =
        UserModel(
            id = row[Users.id],
            username = row[Users.username],
            password = row[Users.password],
            likes = row[Users.likes],
            notLikes = row[Users.notLikes],
            isHater = row[Users.isHater],
            isPromoter = row[Users.isPromoter]
        )
}