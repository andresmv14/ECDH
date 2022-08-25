package com.mv.ecdh.core

import android.annotation.SuppressLint

class Llaves {
    private var secret: String? = null
    private var hash: String? = null
    private var idDev: String? = null

    @SuppressLint("NotConstructor")
    fun Llaves() {}

    fun getSecret(): String? {
        return secret
    }

    fun setSecret(secret: String?) {
        this.secret = secret
    }

    fun getHash(): String? {
        return hash
    }

    fun setHash(hash: String?) {
        this.hash = hash
    }

    fun getIdDev(): String? {
        return idDev
    }

    fun setIdDev(idDev: String?) {
        this.idDev = idDev
    }

    override fun toString(): String {
        return "Llaves [secret=" + secret + ", hash=" + hash + ", idDev=" + idDev + "]"
    }

}
