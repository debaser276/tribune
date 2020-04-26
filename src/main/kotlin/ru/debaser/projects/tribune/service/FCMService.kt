package ru.debaser.projects.tribune.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.apache.commons.codec.binary.Hex
import org.springframework.security.crypto.encrypt.Encryptors
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Paths

class FCMService(
    private val dbUrl: String,
    private val password: String,
    private val salt: String,
    private val path: String
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


}