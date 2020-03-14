package ru.debaser.projects.tribune.service

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.debaser.projects.tribune.repository.UserRepository
import ru.debaser.projects.tribune.dto.AuthenticationRequestDto
import ru.debaser.projects.tribune.dto.AuthenticationResponseDto
import ru.debaser.projects.tribune.model.UserModel

class UserService (
    private val repo: UserRepository,
    private val tokenService: JWTTokenService
) {
    private val mutex = Mutex()

    suspend fun getByUsername(username: String) = repo.getByUsername(username)

    suspend fun register(input: AuthenticationRequestDto): AuthenticationResponseDto {
        mutex.withLock {
            val user = repo.add(UserModel(username = input.username, password = input.password))
            val token = tokenService.generate(user.id)
            return AuthenticationResponseDto(user.id, token)
        }
    }
}