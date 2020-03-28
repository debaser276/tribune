package ru.debaser.projects.tribune.route

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.route
import ru.debaser.projects.tribune.exception.LoginAlreadyExistsException
import ru.debaser.projects.tribune.dto.AuthenticationRequestDto
import ru.debaser.projects.tribune.model.IdeaModel
import ru.debaser.projects.tribune.service.FileService
import ru.debaser.projects.tribune.service.IdeaService
import ru.debaser.projects.tribune.service.UserService

class RoutingV1(
    val staticPath: String,
    val userService: UserService,
    val fileService: FileService,
    val ideaService: IdeaService
) {
    fun setup(configuration: Routing) {
        with(configuration) {
            route("/api/v1") {
                static("/static") {
                    files(staticPath)
                }
                post("/registration") {
                    val input = call.receive<AuthenticationRequestDto>()
                    when(userService.getByUsername(input.username)) {
                        null -> call.respond(userService.register(input))
                        else -> throw LoginAlreadyExistsException()
                    }
                }
                post("/authentication") {
                    val input = call.receive<AuthenticationRequestDto>()
                    call.respond(userService.authenticate(input))
                }
                authenticate {
                    route("/media") {
                        post {
                            val multipart = call.receiveMultipart()
                            val response = fileService.save(multipart)
                            call.respond(response)
                        }
                    }
                    route("ideas") {
                        post {
                            val input = call.receive<IdeaModel>()
                            val id = ideaService.save(input)
                            call.respond(ideaService.getById(id))
                        }
                    }
                }
            }
        }
    }
}