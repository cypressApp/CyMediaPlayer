package com.cypress.cymediaplayer.utils

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtil{
    private val SECRET_KEY = "your_password_here"
    var salt = "your_salt_here"
    private val ALGORITHM = "AES/CBC/PKCS5Padding"

    private fun generateKey(): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(SECRET_KEY.toCharArray(), salt.toByteArray(), 65536, 256)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }

    fun encrypt(strToEncrypt: String): String {
        val iv = ByteArray(16)
        val ivSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, generateKey(), ivSpec)
        val encrypted = cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    fun decrypt(strToDecrypt: String): String {
        val iv = ByteArray(16)
        val ivSpec = IvParameterSpec(iv)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, generateKey(), ivSpec)
        val decoded = Base64.decode(strToDecrypt, Base64.DEFAULT)
        val decrypted = cipher.doFinal(decoded)
        return String(decrypted, Charsets.UTF_8)
    }
}