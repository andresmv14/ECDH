package com.mv.ecdh.core

import android.annotation.SuppressLint

class Mensaje {

    private var publicKey: String? = null
    private var noises: String? = null

    @SuppressLint("NotConstructor")
    fun Mensaje() {}

    fun getPublicKey(): String? {
        return publicKey
    }

    fun setPublicKey(publicKey: String?) {
        this.publicKey = publicKey
    }

    fun getNoises(): String? {
        return noises
    }

    fun setNoises(noises: String?) {
        this.noises = noises
    }

    override fun toString(): String {
        return "Mensaje [publicKey=" + publicKey + ", noises=" + noises + "]"
    }
}