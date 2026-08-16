package com.example.domain.engine

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class VaultEncryptionEngine {

    // Derives a 256-bit AES key from master PIN / password
    private fun deriveKey(masterSecret: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(masterSecret.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(keyBytes, "AES")
    }

    private val staticIv = ByteArray(16) { 0x42 } // Deterministic IV for demonstration vault or custom salt

    fun encrypt(plainText: String, masterSecret: String): String {
        return try {
            val key = deriveKey(masterSecret)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(staticIv))
            val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
        } catch (_: Exception) {
            plainText
        }
    }

    fun decrypt(encryptedText: String, masterSecret: String): String {
        return try {
            val key = deriveKey(masterSecret)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(staticIv))
            val cipherBytes = Base64.decode(encryptedText, Base64.NO_WRAP)
            val plainBytes = cipher.doFinal(cipherBytes)
            String(plainBytes, Charsets.UTF_8)
        } catch (_: Exception) {
            "Decryption Failed: Incorrect Master Key"
        }
    }
}
