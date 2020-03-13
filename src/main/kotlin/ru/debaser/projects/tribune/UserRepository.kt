package ru.debaser.projects.tribune

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.debaser.projects.tribune.model.UserModel

interface UserRepository {
    suspend fun getByid(id: Long): UserModel?
    suspend fun add(user: UserModel): UserModel
    suspend fun save(user: UserModel): UserModel
}

class UserRepositoryInMemoryWithMutex: UserRepository {
    private var nextId = 1L
    private val users = mutableListOf<UserModel>()
    private val mutex = Mutex()

    override suspend fun getByid(id: Long): UserModel? = users.find { it.id == id }

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