package ru.debaser.projects.tribune.statuspages

import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import ru.debaser.projects.tribune.dto.ErrorResponseDto
import ru.debaser.projects.tribune.exception.*


class ErrorHandler {
    fun setup(configuration: StatusPages.Configuration) {
        with (configuration) {
            exception<LoginAlreadyExistsException> {
                call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("Login already exists"))
            }
            exception<DatabaseException> {
                call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("Database exception"))
            }
            exception<UserExistsException> {
                call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("User exists"))
            }
            exception<UserNotFoundException> {
                call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("Username not found"))
            }
            exception<InvalidPasswordException> {
                call.respond(HttpStatusCode.BadRequest, ErrorResponseDto("Password invalid"))
            }
        }
    }
}