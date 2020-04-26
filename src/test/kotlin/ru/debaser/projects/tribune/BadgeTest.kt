package ru.debaser.projects.tribune

import com.jayway.jsonpath.JsonPath
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@KtorExperimentalAPI
class BadgeTest {
    private val configure = Settings.configure

    @Test
    fun testPromoterTrue() {
        withTestApplication(configure) {
            runBlocking {
                val tokens = mutableListOf<String>()
                for (i in 1..10) tokens.add(regToken("user$i"))
                for (i in 0..10) idea("user1", tokens[0])
                for (i in 1..8) like(tokens[1], i)
                for (i in 1..6) like(tokens[2], i)
                for (i in 1..4) like(tokens[3], i)
                with(handleRequest(HttpMethod.Post, "/api/v1/authentication") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"username": "user4","password": "Password"}""")
                }) {
                    val isPromoter = JsonPath.read<Boolean>(response.content, "$.isPromoter")
                    assertTrue(isPromoter)
                }
            }
        }
    }

    @Test
    fun testPromoterTurnToFalse() {
        withTestApplication(configure) {
            runBlocking {
                val token = authToken("user4")
                for (i in 5..7) dislike(token, i)
                with(handleRequest(HttpMethod.Post, "/api/v1/authentication") {
                    addHeader(HttpHeaders.ContentType, jsonContentType.toString())
                    setBody("""{"username": "user4","password": "Password"}""")
                }) {
                    val isPromoter = JsonPath.read<Boolean>(response.content, "$.isPromoter")
                    assertFalse(isPromoter)
                }
            }
        }
    }
}