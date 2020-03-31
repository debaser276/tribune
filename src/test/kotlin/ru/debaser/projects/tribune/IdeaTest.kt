package ru.debaser.projects.tribune

import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.junit.Test

@KtorExperimentalAPI
class IdeaTest {
    val confiure = ReaderTest().configure
    val jsonContentType = ReaderTest().jsonContentType

    @Test
    fun ideas() {
        withTestApplication(confiure) {
            runBlocking {
                val token1 = authToken("user1")
                idea("1", token1)
                idea("1", token1)
                idea("1", token1)
            }
        }
    }
}