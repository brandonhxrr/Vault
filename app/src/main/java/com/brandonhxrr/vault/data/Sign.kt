package com.brandonhxrr.vault.data

import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun signECDSA(privateKeyBytes: ByteArray, data: ByteArray): String {
    val kf = KeyFactory.getInstance("EC")
    val privateKey = kf.generatePrivate(PKCS8EncodedKeySpec(privateKeyBytes))

    val signature = Signature.getInstance("SHA256withECDSA")
    signature.initSign(privateKey)
    signature.update(data)
    return Base64.getEncoder().encodeToString(signature.sign())
}

fun verifyECDSA(publicKeyBytes: ByteArray, data: ByteArray, signature: ByteArray): Boolean {

    val kf = KeyFactory.getInstance("EC")

    val keySpecPublic = X509EncodedKeySpec(publicKeyBytes)
    val publicKey= kf.generatePublic(keySpecPublic)

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