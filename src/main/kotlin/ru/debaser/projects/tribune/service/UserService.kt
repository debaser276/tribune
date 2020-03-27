package ru.debaser.projects.tribune.service

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.security.crypto.password.PasswordEncoder
import ru.debaser.projects.tribune.repository.UserRepository
import ru.debaser.projects.tribune.dto.AuthenticationRequestDto
import ru.debaser.projects.tribune.dto.AuthenticationResponseDto
import ru.debaser.projects.tribune.exception.DatabaseException
import ru.debaser.projects.tribune.exception.InvalidPasswordException
import ru.debaser.projects.tribune.exception.UserExistsException
import ru.debaser.projects.tribune.exception.UserNotFoundException
import ru.debaser.projects.tribune.model.UserModel

class UserService (
    private val repo: UserRepository,
    private val tokenService: JWTTokenService,
    private val passwordEncoder: PasswordEncoder
) {
    private val mutex = Mutex()

    suspend fun getByUsername(username: String): UserModel? = repo.getByUsername(username)

    suspend fun register(input: AuthenticationRequestDto): AuthenticationResponseDto {
        mutex.withLock {
            if (repo.getByUsername(input.username) != null) {
                throw UserExistsException()
            }
            val id = repo.save(UserModel(
                username = input.username,
                password = passwordEncoder.encode(input.password)
            )) ?: throw DatabaseException()
            val token = tokenService.generate(id)
            return AuthenticationResponseDto(id, token)
        }
    }

    suspend fun authenticate(input: AuthenticationRequestDto): AuthenticationResponseDto {
        val model = repo.getByUsername(input.username) ?: throw UserNotFoundException()
        if (!passwordEncoder.matches(input.password, model.password)) {
            throw InvalidPasswordException()
        }
        val token = tokenService.generate(model.id)
        return AuthenticationResponseDto(model.id, token)
    }
}