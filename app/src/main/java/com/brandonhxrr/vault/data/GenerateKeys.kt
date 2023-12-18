package com.brandonhxrr.vault.data

import android.content.Context
import org.bouncycastle.jce.ECNamedCurveTable
import java.io.File
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

fun generateKeys(context: Context) {
    val keyPair = generateECDSAKeyPair()
    val privateKeyFile = File(context.filesDir, "private_key.pem")
    val publicKeyFile = File(context.filesDir, "public_key.pem")

    savePrivateKeyToFile(privateKeyFile, keyPair.private.encoded)
    savePublicKeyToFile(publicKeyFile, keyPair.public.encoded)

    println("Llaves generadas y guardadas exitosamente.")
}

/*fun sharePublicKey(context: Context) {
    val publicKeyFile = File(context.filesDir, "public_key.pem")

    val publicKey = loadPublicKeyFromFile(publicKeyFile)

    println("La clave pública es: ${publicKey.toBase64String()}")
}*/

fun generateECDSAKeyPair(): KeyPair {
    val keyGen = KeyPairGenerator.getInstance("EC")
    val keyGenSpec = ECGenParameterSpec("secp256r1")
    keyGen.initialize(keyGenSpec)
    return keyGen.generateKeyPair()
}

fun savePrivateKeyToFile(file: File, privateKey: ByteArray) {
    file.writeBytes(privateKey)
}

fun savePublicKeyToFile(file: File, publicKey: ByteArray) {
    file.writeBytes(publicKey)
}

fun loadPrivateKeyFromFile(file: File): PrivateKey {

    val keyBytes = file.readBytes()
    val keyFactory = KeyFactory.getInstance("EC")
    val privateKeySpec = PKCS8EncodedKeySpec(keyBytes)
    return keyFactory.generatePrivate(privateKeySpec)
}

fun loadPublicKeyFromFile(context: Context): String {
    val publicKeyFile = File(context.filesDir, "public_key.pem")
    val publicKeyBytes = publicKeyFile.readBytes()

    return Base64.getEncoder().encodeToString(publicKeyBytes)
}

fun performECDHKeyExchange(privateKey: PrivateKey, otherPartyPublicKey: ByteArray): ByteArray {
    val kf = KeyFactory.getInstance("EC")
    val curveParams = ECNamedCurveTable.getParameterSpec("secp256r1")
    val keySpec = X509EncodedKeySpec(otherPartyPublicKey)
    val publicKey = kf.generatePublic(keySpec)

    val keyAgreement = KeyAgreement.getInstance("ECDH")
    keyAgreement.init(privateKey)
    keyAgreement.doPhase(publicKey, true)
    return keyAgreement.generateSecret()
}

fun generateAESKeyFromSharedSecret(sharedSecret: ByteArray): SecretKeySpec {
    val salt = "salt_123".toByteArray()
    val iterations = 100000
    val keyLength = 256 // Longitud de la clave AES (AES-256)
    val keySpec = PBEKeySpec(String(sharedSecret).toCharArray(), salt, iterations, keyLength)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val secretKey = factory.generateSecret(keySpec)
    return SecretKeySpec(secretKey.encoded, "AES")
}

/*fun performKeyExchange(context: Context) {
    //Agregar que ingrese la llave del otro usuario
    val privateKeyFile = File(context.filesDir, "private_key.pem")
    val publicKeyFile = File(context.filesDir, "public_key.pem")
    val aesKeyFile = File(context.filesDir, "aes_key.txt")

    val loadedPrivateKey = loadPrivateKeyFromFile(privateKeyFile)
    val otherPartyPublicKey = loadPublicKeyFromFile(publicKeyFile)

    val sharedSecret = performECDHKeyExchange(loadedPrivateKey, otherPartyPublicKey)
    val aesKey = generateAESKeyFromSharedSecret(sharedSecret)

    saveAESKeyToFile(aesKeyFile, aesKey.encoded)

    println("Intercambio de llaves completado. Clave AES generada y guardada en '$aesKeyFile'.")
}*/

fun saveAESKeyToFile(file: File, aesKey: ByteArray) {
    val base64AESKey = Base64.getEncoder().encodeToString(aesKey)
    file.writeText(base64AESKey)
}

fun ByteArray.toBase64String(): String = Base64.getEncoder().encodeToString(this)

fun String.base64ToByteArray(): ByteArray = Base64.getDecoder().decode(this)
