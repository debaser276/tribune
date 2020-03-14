package ru.debaser.projects.tribune.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.apache.commons.codec.binary.Hex
import org.springframework.security.crypto.encrypt.Encryptors
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class JWTTokenService (
    private val password: String,
    private val salt: String,
    private val path: String
) {
    private lateinit var secret: String

    init {
        val decryptor = Encryptors.stronger(password, Hex.encodeHexString(salt.toByteArray(Charsets.UTF_8)))
        secret = String(decryptor.decrypt(Files.readAllBytes(Paths.get(path))))
    }

    private val algo = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algo).build()

    fun generate(id: Long): String = JWT.create()
        .withClaim("id", id)
        .withExpiresAt(Calendar.getInstance().apply { add(Calendar.HOUR, 1) }.time)
        .sign(algo)
}