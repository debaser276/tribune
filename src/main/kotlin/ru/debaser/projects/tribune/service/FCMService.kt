package ru.debaser.projects.tribune.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.apache.commons.codec.binary.Hex
import org.springframework.security.crypto.encrypt.Encryptors
import java.io.ByteArrayInputStream
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Paths

class FCMService(
    dbUrl: String,
    password: String,
    salt: String,
    path: String
) {
    init {
        val decryptor = Encryptors.stronger(password, Hex.encodeHexString(salt.toByteArray(Charsets.UTF_8)))
        val decrypted = decryptor.decrypt(Files.readAllBytes(Paths.get(path)))

        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(ByteArrayInputStream(decrypted)))
            .setDatabaseUrl(dbUrl)
            .build()

        FirebaseApp.initializeApp(options)
    }

    suspend fun sendVote(userLiked: String, recipient: String, ideaContent: String, isUp: Boolean) {
        try {
            val message = Message.builder()
                .putData("type", "vote")
                .putData("title", "Vote")
                .putData("content", "$userLiked ${if (isUp) "liked" else "disliked"} $ideaContent")
                .setToken(recipient)
                .build()
            FirebaseMessaging.getInstance().send(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}