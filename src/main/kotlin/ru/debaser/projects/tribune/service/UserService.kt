package ru.debaser.projects.tribune.service

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.debaser.projects.tribune.repository.UserRepository
import ru.debaser.projects.tribune.dto.AuthenticationRequestDto
import ru.debaser.projects.tribune.dto.AuthenticationResponseDto
import ru.debaser.projects.tribune.exception.DatabaseException
import ru.debaser.projects.tribune.model.UserModel

class UserService (
    private val repo: UserRepository,
    private val tokenService: JWTTokenService
) {
    private val mutex = Mutex()

    suspend fun getByUsername(username: String): UserModel? = repo.getByUsername(username)

    suspend fun register(input: AuthenticationRequestDto): AuthenticationResponseDto {
        mutex.withLock {
            val id = repo.save(UserModel(
                username = input.username,
                password = input.password)) ?: throw DatabaseException()
            val token = tokenService.generate(id)
            return AuthenticationResponseDto(id, token)
        }
    }
}