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
}

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
    val keySpec = X509EncodedKeySpec(otherPartyPublicKey)
    val publicKey = kf.generatePublic(keySpec)

    val keyAgreement = KeyAgreement.getInstance("ECDH")
    keyAgreement.init(privateKey)
    keyAgreement.doPhase(publicKey, true)
    return keyAgreement.generateSecret()
}