package ru.debaser.projects.tribune

import com.jayway.jsonpath.JsonPath
import io.ktor.application.Application
import io.ktor.config.MapApplicationConfig
import io.ktor.http.*
import io.ktor.server.testing.*
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
            put("tribune.settings.result-size", "2")
        }
        module()
    }

    @Test
    fun testAuth() {
        withTestApplication(configure) {
            runBlocking {
                regUser("user1")
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
                val token2 = regUser("user2")
                val token3 = regUser("user3")
                val token4 = regUser("user4")
                handleRequest(HttpMethod.Post, "/api/v1/ideas") {
                    addHeader(HttpHeaders.Authorization, "Bearer $token2")
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"authorId": 2,"content": "New Idea","media": "{d9e0e38e-ef6f-4e62-9191-e97bad6be0b8.jpg"}""")
                }
                vote("dislike", token3)
                vote("dislike", token4)
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
                val token5 = regUser("user5")
                vote("like", token5)
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

    private fun TestApplicationEngine.regUser(username: String): String {
        with(handleRequest(HttpMethod.Post, "/api/v1/registration") {
            addHeader(HttpHeaders.ContentType, jsonContentType.toString())
            setBody("""{"username": "$username","password": "Password"}""")
        }) {
            return JsonPath.read<String>(response.content!!, "$.token")
        }
    }

    private fun TestApplicationEngine.vote(vote: String, token: String) {
        handleRequest(HttpMethod.Put, "/api/v1/ideas/1/$vote") {
            addHeader(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}