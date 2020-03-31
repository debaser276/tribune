package ru.debaser.projects.tribune.route

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.ParameterConversionException
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.util.pipeline.PipelineContext
import ru.debaser.projects.tribune.exception.LoginAlreadyExistsException
import ru.debaser.projects.tribune.dto.AuthenticationRequestDto
import ru.debaser.projects.tribune.model.IdeaModel
import ru.debaser.projects.tribune.model.UserModel
import ru.debaser.projects.tribune.service.FileService
import ru.debaser.projects.tribune.service.IdeaService
import ru.debaser.projects.tribune.service.UserService

val <T: Any> PipelineContext<T, ApplicationCall>.id
    get() = call.parameters["id"]?.toLongOrNull() ?: throw ParameterConversionException("id", "Long")

val <T: Any> PipelineContext<T, ApplicationCall>.me
    get() = call.authentication.principal<UserModel>()

class RoutingV1(
    private val staticPath: String,
    private val userService: UserService,
    private val fileService: FileService,
    private val ideaService: IdeaService
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
                    route("/ideas") {
                        post {
                            val input = call.receive<IdeaModel>()
                            val id = ideaService.postIdea(input)
                            call.respond(ideaService.getById(id))
                        }
                        put("/{id}/like") {
                            val response = ideaService.like(id, me!!.id)
                            val authorId = ideaService.getById(id).authorId
                            if (!ideaService.isReaderEnough(id) && userService.isReader(authorId)) {
                                userService.reader(authorId, false)
                            }
                            call.respond(response)
                        }
                        put("/{id}/dislike") {
                            val response = ideaService.dislike(id, me!!.id)
                            val authorId = ideaService.getById(id).authorId
                            if (ideaService.isReaderEnough(id) && !userService.isReader(authorId)) {
                                userService.reader(authorId, true)
                            }
                            call.respond(response)
                        }
                    }
                }
            }
        }
    }
}