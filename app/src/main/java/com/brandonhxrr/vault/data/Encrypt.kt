package com.brandonhxrr.vault.data

import android.content.Context
import java.io.File
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


fun loadKeyFromFile(filePath: String): SecretKey {
    val base64Key = File(filePath).readText() // Lee la clave en formato base64 desde el archivo
    val keyBytes = Base64.getDecoder().decode(base64Key) // Decodifica base64 a bytes

    return SecretKeySpec(keyBytes, "AES") // Crea una instancia de SecretKey con los bytes decodificados
}

fun encryptFileAesGcm(context: Context, sharedKey: ByteArray, file: File): File {
    val content = file.readBytes()

    val secretKey = SecretKeySpec(sharedKey, "AES")
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)

    val iv = cipher.iv
    val cipherText = cipher.doFinal(content)

    val encryptedFile = File(context.filesDir, file.nameWithoutExtension+ "_temp_enc"+file.extension)
    val encryptedData = ByteArray(iv.size + cipherText.size)
    System.arraycopy(iv, 0, encryptedData, 0, iv.size)
    System.arraycopy(cipherText, 0, encryptedData, iv.size, cipherText.size)

    encryptedFile.writeBytes(Base64.getEncoder().encode(encryptedData))

    return encryptedFile
}
fun decryptFileAesGcm(sharedKey: ByteArray, encryptedData: ByteArray): ByteArray {
    val content = Base64.getDecoder().decode(encryptedData)

    val secretKey = SecretKeySpec(sharedKey, "AES")

    val iv = content.sliceArray(0 until 12)
    val cipherText = content.sliceArray(12 until content.size)

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val spec = GCMParameterSpec(128, iv)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

    return cipher.doFinal(cipherText)
}



