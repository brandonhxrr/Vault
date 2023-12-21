package com.brandonhxrr.vault.data

import android.content.Context
import android.util.Log
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.HKDFParameters
import org.bouncycastle.jce.ECNamedCurveTable
import java.io.File
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Arrays
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

fun loadPrivateKeyFromFile(context: Context): String {
    val privateKeyFile = File(context.filesDir, "private_key.pem")
    val keyBytes = privateKeyFile.readBytes()
    return Base64.getEncoder().encodeToString(keyBytes)
}

fun loadPublicKeyFromFile(context: Context): String {
    val publicKeyFile = File(context.filesDir, "public_key.pem")
    val publicKeyBytes = publicKeyFile.readBytes()

    return Base64.getEncoder().encodeToString(publicKeyBytes)
}

fun performECDHKeyExchange(privateKeyBytes: ByteArray, otherPartyPublicKey: ByteArray): ByteArray {
    val kf = KeyFactory.getInstance("EC")
    val keySpecPrivate = PKCS8EncodedKeySpec(privateKeyBytes)
    val privateKey = kf.generatePrivate(keySpecPrivate)

    val keySpecPublic = X509EncodedKeySpec(otherPartyPublicKey)
    val otherPartyPublicKey = kf.generatePublic(keySpecPublic)

    val keyAgreement = KeyAgreement.getInstance("ECDH")
    keyAgreement.init(privateKey)
    keyAgreement.doPhase(otherPartyPublicKey, true)

    val sharedSecret = keyAgreement.generateSecret()
    return deriveKeyHKDF(sharedSecret, "AES/GCM/NoPadding")
}

fun deriveKeyHKDF(secret: ByteArray, algorithm: String): ByteArray {
    val hkdf = HKDFBytesGenerator(SHA256Digest())
    val params = HKDFParameters(secret, null, null)
    hkdf.init(params)
    val output = ByteArray(32)
    hkdf.generateBytes(output, 0, output.size)
    return output
}