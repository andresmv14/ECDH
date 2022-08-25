package com.mv.ecdh.core

import android.annotation.SuppressLint
import com.google.gson.JsonElement

class KeyExchange {
    private var key: String? = null
    private var cod = 0
    private var mensaje: String? = null

    @SuppressLint("NotConstructor")
    fun KeyExchange() {}

    fun getKey(): String? {
        return key
    }

    fun setKey(key: String?) {
        this.key = key
    }

    fun getCod(): Int {
        return cod
    }

    fun setCod(cod: Int) {
        this.cod = cod
    }

    fun getMensaje(): String? {
        return mensaje
    }

    fun setMensaje(mensaje: String?) {
        this.mensaje = mensaje
    }

    override fun toString(): String {
        return "KeyExchange [key=" + key + ", cod=" + cod + ", mensaje=" + mensaje + "]"
    }
}
