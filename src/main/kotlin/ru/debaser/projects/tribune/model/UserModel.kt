package ru.debaser.projects.tribune.model

data class UserModel(
    val id: Long,
    val username: String,
    val password: String,
    val likedCount: Int = 0,
    val notLikedCount: Int = 0,
    val isHater: Boolean = false,
    val isPromoter: Boolean = false
)