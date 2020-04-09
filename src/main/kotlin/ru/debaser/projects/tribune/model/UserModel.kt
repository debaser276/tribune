package ru.debaser.projects.tribune.model

import io.ktor.auth.Principal

data class UserModel(
    val id: Long = 1L,
    val username: String,
    val password: String,
    val isHater: Boolean = false,
    val isPromoter: Boolean = false,
    val isReader: Boolean = false,
    val avatar: String = ""
): Principal