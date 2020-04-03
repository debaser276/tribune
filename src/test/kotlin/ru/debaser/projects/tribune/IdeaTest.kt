package ru.debaser.projects.tribune

import io.ktor.server.testing.withTestApplication
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import org.junit.Test

@KtorExperimentalAPI
class IdeaTest {
    private val configure = Settings.configure

    @Test
    fun ideas() {
        withTestApplication(configure) {
            runBlocking {
                val token1 = authToken("user1")
                val token2 = authToken("user2")
                val token3 = authToken("user3")
                idea("1", token1)
                idea("2", token2)
                idea("3", token3)
            }
        }
    }
}