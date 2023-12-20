package com.brandonhxrr.vault.data

import java.nio.file.Files
import java.nio.file.Paths
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun signECDSA(privateKey: PrivateKey, filePath: String): ByteArray {
    val data = Files.readAllBytes(Paths.get(filePath))
    val signature = Signature.getInstance("SHA256withECDSA")
    signature.initSign(privateKey)
    signature.update(data)
    return signature.sign()
}

fun verifyECDSA(publicKey: PublicKey, filePath: String, signature: ByteArray): Boolean {
    val data = Files.readAllBytes(Paths.get(filePath))
    val verifySignature = Signature.getInstance("SHA256withECDSA")
    verifySignature.initVerify(publicKey)
    verifySignature.update(data)
    return verifySignature.verify(signature)
}

fun generateHMAC(key: ByteArray, filePath: String): ByteArray {
    val data = Files.readAllBytes(Paths.get(filePath))
    val hmac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(key, "HmacSHA256")
    hmac.init(secretKey)
    return hmac.doFinal(data)
}

fun verifyHMAC(hmac: ByteArray, sharedKey: ByteArray, filePath: String): Boolean {
    val generatedHMAC = generateHMAC(sharedKey, filePath)
    return hmac.contentEquals(generatedHMAC)
}