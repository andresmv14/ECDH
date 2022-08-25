package com.mv.ecdh.core

import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.KeyAgreement
import javax.xml.bind.DatatypeConverter

class Ecdh {
    fun ecdh() {}

    @Throws(
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        UnsupportedEncodingException::class,
        InvalidKeySpecException::class,
        NoSuchProviderException::class
    )
    fun getSecret(mensaje: Mensaje): Mensaje? {
        val secret = Mensaje()
        val kp: KeyPair? = getKeys()
        val sharedSC: ByteArray? = kp?.let { this.getSecretPass(mensaje.getPublicKey(), it) }
        val ourPk: ByteArray = kp?.getPublic()!!.getEncoded()
        System.out.println("Public Key: " + DatatypeConverter.printHexBinary(ourPk))
        secret.setPublicKey(DatatypeConverter.printHexBinary(ourPk))
        val otherPk: ByteArray = DatatypeConverter.parseHexBinary(mensaje.getPublicKey())
        System.out.println("Ingresa la otra llave: " + DatatypeConverter.printHexBinary(otherPk))
        val sharedSCStr: String = DatatypeConverter.printHexBinary(sharedSC)
        println("Shared secret: $sharedSCStr")
        val salt = getHashDerivedKey(sharedSC, ourPk, otherPk)
        println("Final key: $salt")
        return secret
    }

    @Throws(NoSuchAlgorithmException::class)
    fun getHashDerivedKey(
        sharedSecret: ByteArray?,
        ourPk: ByteArray?,
        otherPk: ByteArray?
    ): String? {
        var llavederivada: String? = null
        val hash: MessageDigest = MessageDigest.getInstance("SHA-256")
        hash.update(sharedSecret)
        val keys: List<ByteBuffer> = Arrays.asList(ByteBuffer.wrap(ourPk), ByteBuffer.wrap(otherPk))
        Collections.sort(keys)
        hash.update(keys[0] as ByteBuffer)
        hash.update(keys[1] as ByteBuffer)
        val derivedKey: ByteArray = hash.digest()
        llavederivada = DatatypeConverter.printHexBinary(derivedKey)
        return llavederivada
    }

    fun getKeys(): KeyPair? {
        var kp: KeyPair? = null
        val kpg: KeyPairGenerator = KeyPairGenerator.getInstance("EC")
        kpg.initialize(256)
        kp = kpg.generateKeyPair()
        return kp
    }



    fun getSecretPass(publicKey: String?, kp: KeyPair): ByteArray? {
        val sharedSecret: ByteArray? = null
        val kf: KeyFactory = KeyFactory.getInstance("EC")
        val pkSpec =
            X509EncodedKeySpec(DatatypeConverter.parseHexBinary(publicKey))
        val otherPublicKey: PublicKey = kf.generatePublic(pkSpec)
        val ka: KeyAgreement = KeyAgreement.getInstance("ECDH")
        ka.init(kp.getPrivate())
        ka.doPhase(otherPublicKey, true)
        return ka.generateSecret()
    }



}