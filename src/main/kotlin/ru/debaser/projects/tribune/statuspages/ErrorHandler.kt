package ru.debaser.projects.tribune.statuspages

import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import ru.debaser.projects.tribune.exception.LoginAlreadyExistsException
import ru.debaser.projects.tribune.dto.ErrorResponseDto


class ErrorHandler {
    fun setup(configuration: StatusPages.Configuration) {
        with (configuration) {
            exception<LoginAlreadyExistsException>() {
                call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("Login already exists"))
            }
        }
    }
}