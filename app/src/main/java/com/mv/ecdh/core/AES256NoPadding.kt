package com.mv.ecdh.core

import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.charset.Charset
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AES256NoPadding {
    private val GCM_IV_LENGTH = 12
    private val GCM_TAG_LENGTH = 16

    fun AES256NoPadding() {}

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getSecretKeySpec(keySecret: String, salt: String): SecretKeySpec? {
        var secretKey: SecretKeySpec? = null
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(keySecret.toCharArray(), salt.toByteArray(), 65536, 256)
        val tmp: SecretKey = factory.generateSecret(spec)
        secretKey = SecretKeySpec(tmp.getEncoded(), "AES")
        return secretKey
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun encrypt(privateString: String, secretKey: SecretKeySpec?): String? {
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val ivSpec = GCMParameterSpec(128, iv)
        cipher.init(1, secretKey, ivSpec)
        val ciphertext: ByteArray =
            cipher.doFinal(privateString.toByteArray(charset("UTF8")))
        val encrypted = ByteArray(iv.size + ciphertext.size)
        System.arraycopy(iv, 0, encrypted, 0, iv.size)
        System.arraycopy(ciphertext, 0, encrypted, iv.size, ciphertext.size)
        return Base64.getEncoder().encodeToString(encrypted)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(encrypted: String?, secretKey: SecretKeySpec?): String {
        val decoded: ByteArray = Base64.getDecoder().decode(encrypted)
        val iv: ByteArray = Arrays.copyOfRange(decoded, 0, 12)
        val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val ivSpec = GCMParameterSpec(128, iv)
        cipher.init(2, secretKey, ivSpec)
        val ciphertext: ByteArray = cipher.doFinal(decoded, 12, decoded.size - 12)
        return String(ciphertext, Charset.forName("UTF8"))
    }

}
