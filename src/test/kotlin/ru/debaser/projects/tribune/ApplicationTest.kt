package ru.debaser.projects.tribune

import com.jayway.jsonpath.JsonPath
import io.ktor.application.Application
import io.ktor.config.MapApplicationConfig
import io.ktor.http.*
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.json
import org.junit.Test
import java.nio.file.Files
import kotlin.test.assertEquals

@KtorExperimentalAPI
class ApplicationTest {
    private val jsonContentType = ContentType.Application.Json.withCharset(Charsets.UTF_8)
    private val uploadPath = Files.createTempDirectory("test").toString()
    private val configure: Application.() -> Unit = {
        (environment.config as MapApplicationConfig).apply {
            put("tribune.upload.dir", uploadPath)
            put("tribune.jwt.secret", "2875f2518dd74feeb3260ebe1d24cb09")
            put("tribune.db.jdbcUrl", "postgres://debaser:password@localhost:54321/test")
        }
        module()
    }

    @Test
    fun testRegister() {
        withTestApplication(configure) {
            runBlocking {
                var token: String?
                with(handleRequest(HttpMethod.Post, "/api/v1/registration") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody(
                        """
                            {
                                "username": "user1",
                                "password": "Password"
                            }
                        """.trimIndent()
                    )
                }) {
                    assertEquals(HttpStatusCode.OK, response.status())
                    token = JsonPath.read<String>(response.content!!, "$.token")
                }
            }
        }
    }

}