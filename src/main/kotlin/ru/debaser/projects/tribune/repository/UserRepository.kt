package ru.debaser.projects.tribune.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.debaser.projects.tribune.dto.AuthenticationResponseDto
import ru.debaser.projects.tribune.model.UserModel

interface UserRepository {
    suspend fun getByUsername(username: String): UserModel?
    suspend fun add(user: UserModel): UserModel
    suspend fun save(user: UserModel): UserModel
}

class UserRepositoryInMemoryWithMutex: UserRepository {
    private var nextId = 1L
    private val users = mutableListOf<UserModel>()
    private val mutex = Mutex()

    override suspend fun getByUsername(username: String): UserModel? =
        users.find { it.username == username }

    override suspend fun add(user: UserModel): UserModel {
        mutex.withLock {
            val copy = user.copy(id = nextId++)
            users.add(copy)
            return copy
        }
    }

    override suspend fun save(user: UserModel): UserModel {
        mutex.withLock {
            return when(val index = users.indexOfFirst { it.id == user.id }) {
                -1 -> {
                    val copy = user.copy(id = nextId++)
                    users.add(copy)
                    copy
                }
                else -> {
                    users[index] = user
                    user
                }
            }
        }
    }
}