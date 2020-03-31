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
import org.junit.Test
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@KtorExperimentalAPI
class ApplicationTest {
    private val jsonContentType = ContentType.Application.Json.withCharset(Charsets.UTF_8)
    private val uploadPath = Files.createTempDirectory("test").toString()
    private val configure: Application.() -> Unit = {
        (environment.config as MapApplicationConfig).apply {
            put("tribune.upload.dir", uploadPath)
            put("tribune.jwt.secret", "2875f2518dd74feeb3260ebe1d24cb09")
            put("tribune.db.jdbcUrl", "postgres://debaser:password@localhost:54321/test")
            put("tribune.settings.reader-dislikes", "1")
        }
        module()
    }

    @Test
    fun testAuth() {
        withTestApplication(configure) {
            runBlocking {
                handleRequest(HttpMethod.Post, "/api/v1/registration") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"username": "user1","password": "Password"}""")
                }
                var token: String?
                with(handleRequest(HttpMethod.Post, "/api/v1/authentication") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"username": "user1","password": "Password"}""")
                }) {
                    val username = JsonPath.read<Int>(response.content, "$.id")
                    assertEquals("1", username.toString())
                }
            }
        }
    }

    @Test
    fun testReader() {
        withTestApplication(configure) {
            runBlocking {
                var token2: String?
                with(handleRequest(HttpMethod.Post, "/api/v1/registration") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"username": "user2","password": "Password"}""")
                }) {
                    token2 = JsonPath.read<String>(response.content!!, "$.token")
                }
                var token3: String?
                with(handleRequest(HttpMethod.Post, "/api/v1/registration") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"username": "user3","password": "Password"}""")
                }) {
                    token3 = JsonPath.read<String>(response.content!!, "$.token")
                }
                var token4: String?
                with(handleRequest(HttpMethod.Post, "/api/v1/registration") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"username": "user4","password": "Password"}""")
                }) {
                    token4 = JsonPath.read<String>(response.content!!, "$.token")
                }
                handleRequest(HttpMethod.Post, "/api/v1/ideas") {
                    addHeader(HttpHeaders.Authorization, "Bearer $token2")
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"authorId": 2,"content": "New Idea","media": "{d9e0e38e-ef6f-4e62-9191-e97bad6be0b8.jpg"}""")
                }
                handleRequest(HttpMethod.Put, "/api/v1/ideas/1/dislike") {
                    addHeader(HttpHeaders.Authorization, "Bearer $token3")
                }
                handleRequest(HttpMethod.Put, "/api/v1/ideas/1/dislike") {
                    addHeader(HttpHeaders.Authorization, "Bearer $token4")
                }
                with(handleRequest(HttpMethod.Post, "/api/v1/authentication") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"username": "user2","password": "Password"}""")
                }) {
                    val isReader = JsonPath.read<Boolean>(response.content, "$.isReader")
                    assertTrue(isReader)
                }
            }
        }
    }

    @Test
    fun testNotReader() {
        withTestApplication(configure) {
            runBlocking {
                var token5: String?
                with(handleRequest(HttpMethod.Post, "/api/v1/registration") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"username": "user5","password": "Password"}""")
                }) {
                    token5 = JsonPath.read<String>(response.content!!, "$.token")
                }
                handleRequest(HttpMethod.Put, "/api/v1/ideas/1/like") {
                    addHeader(HttpHeaders.Authorization, "Bearer $token5")
                }
                with(handleRequest(HttpMethod.Post, "/api/v1/authentication") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"username": "user2","password": "Password"}""")
                }) {
                    val isReader = JsonPath.read<Boolean>(response.content, "$.isReader")
                    assertFalse(isReader)
                }
            }
        }
    }

}