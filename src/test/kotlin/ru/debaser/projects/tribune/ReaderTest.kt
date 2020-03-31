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
class ReaderTest {
    val configure = Settings.configure
    val jsonContentType = Settings.jsonContentType

    @Test
    fun testAuth() {
        withTestApplication(configure) {
            runBlocking {
                regToken("user1")
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
                val token2 = regToken("user2")
                val token3 = regToken("user3")
                val token4 = regToken("user4")
                handleRequest(HttpMethod.Post, "/api/v1/ideas") {
                    addHeader(HttpHeaders.Authorization, "Bearer $token2")
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"authorId": 2,"content": "New Idea","media": "{d9e0e38e-ef6f-4e62-9191-e97bad6be0b8.jpg"}""")
                }
                vote("1", "dislike", token3)
                vote("1", "dislike", token4)
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
                val token5 = regToken("user5")
                vote("1", "like", token5)
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