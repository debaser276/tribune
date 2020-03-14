package ru.debaser.projects.tribune

import org.apache.commons.codec.binary.Hex
import org.springframework.security.crypto.encrypt.Encryptors
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*

fun main() {
    val properties = Properties().apply {
        load(Files.newBufferedReader(Paths.get("./secrets/encrypt.properties")))
    }
    val encryptor = Encryptors.stronger(properties.getProperty("password"), Hex.encodeHexString(properties.getProperty("salt").toByteArray(Charsets.UTF_8)))
    val encrypted = encryptor.encrypt(Files.readAllBytes(Paths.get("./secrets/jwtsecret")))
    Files.write(Paths.get("./secrets/jwtsecret-encrypted"), encrypted, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

    val decryptor = Encryptors.stronger(properties.getProperty("password"), Hex.encodeHexString(properties.getProperty("salt").toByteArray(Charsets.UTF_8)))
    val decrypted = String(decryptor.decrypt(Files.readAllBytes(Paths.get("./secrets/jwtsecret-encrypted"))))
    println(decrypted)
}