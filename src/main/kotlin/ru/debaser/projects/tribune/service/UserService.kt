package ru.debaser.projects.tribune.service

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

    suspend fun getByUsername(username: String): UserModel? = repo.getByUsername(username)

    suspend fun getById(id: Long): UserModel? = repo.getById(id)

    suspend fun register(input: AuthenticationRequestDto): AuthenticationResponseDto {
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

    suspend fun authenticate(input: AuthenticationRequestDto): AuthenticationResponseDto {
        val model = repo.getByUsername(input.username) ?: throw UserNotFoundException()
        if (!passwordEncoder.matches(input.password, model.password)) {
            throw InvalidPasswordException()
        }
        val token = tokenService.generate(model.id)
        return AuthenticationResponseDto.fromModel(model, token)
    }

    suspend fun setReader(id: Long, set: Boolean) = repo.setReader(id, set)

    suspend fun isReader(id: Long) = repo.isReader(id)

    suspend fun isPromoter(id: Long) = repo.isPromoter(id)

    suspend fun setPromoter(id: Long, set: Boolean) = repo.setPromoter(id, set)

    suspend fun isHater(id: Long) = repo.isHater(id)

    suspend fun setHater(id: Long, set: Boolean) = repo.setHater(id, set)

    suspend fun addAvatar(id: Long, imageId: String) = repo.addAvatar(id, imageId)
}