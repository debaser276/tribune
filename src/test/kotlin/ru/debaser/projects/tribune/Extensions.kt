package ru.debaser.projects.tribune

import com.jayway.jsonpath.JsonPath
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody

val jsonContentType = Settings.jsonContentType

fun TestApplicationEngine.regToken(username: String): String {
    with(handleRequest(HttpMethod.Post, "/api/v1/registration") {
        addHeader(HttpHeaders.ContentType, jsonContentType.toString())
        setBody("""{"username": "$username","password": "Password"}""")
    }) {
        return JsonPath.read<String>(response.content!!, "$.token")
    }
}

fun TestApplicationEngine.vote(ideaId: String, vote: String, token: String) {
    handleRequest(HttpMethod.Put, "/api/v1/ideas/$ideaId/$vote") {
        addHeader(HttpHeaders.Authorization, "Bearer $token")
    }
}

//fun TestApplicationEngine.authToken(username: String): String {
//    with(handleRequest(HttpMethod.Post, "/api/v1/authentication") {
//        addHeader(HttpHeaders.ContentType, jsonContentType.toString())
//        setBody("""{"username": "$username","password": "Password"}""")
//    }) {
//        return JsonPath.read<String>(response.content!!, "$.token")
//    }
//}

//fun TestApplicationEngine.idea(authorId: String, token: String) {
//    handleRequest(HttpMethod.Post, "/api/v1/ideas") {
//        addHeader(HttpHeaders.Authorization, "Bearer $token")
//        addHeader(HttpHeaders.ContentType, jsonContentType.toString())
//        setBody("""{"authorId": $authorId,"content": "New Idea","media": "{d9e0e38e-ef6f-4e62-9191-e97bad6be0b8.jpg"}""")
//    }
//}