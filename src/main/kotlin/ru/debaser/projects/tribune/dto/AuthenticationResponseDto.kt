package ru.debaser.projects.tribune.dto

import ru.debaser.projects.tribune.model.UserModel

data class AuthenticationResponseDto(
    val id: Long,
    val token: String,
    val isHater: Boolean? = null,
    val isPromoter: Boolean? = null,
    val isReader: Boolean? = null)
{
    companion object {
        fun fromModel(user: UserModel, token: String) = AuthenticationResponseDto(
            id = user.id,
            token = token,
            isHater = user.isHater,
            isPromoter = user.isPromoter,
            isReader = user.isReader
        )
    }
}