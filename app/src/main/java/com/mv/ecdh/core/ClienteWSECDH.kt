package com.mv.ecdh.core

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import okhttp3.*
import java.io.*
import java.security.KeyPair
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter

class ClienteWSECDH {
    var client = OkHttpClient()

    fun post(url: String?, idDev: String?, json: String?): String {
        val body = RequestBody.create(JSON,json)
        val request: Request = Request.Builder().url(url).addHeader("IdDev", idDev).post(body).build()
        var var6: Throwable? = null
        val var7: Any? = null
        return try {
            val response: Response? = client.newCall(request).execute()
            val var10000: String
            var10000 = try {
                response?.body().toString()
            } finally {
                if (response != null) {
                    response.close()
                }
            }
            var10000
        } catch (var14: Throwable) {
            if (var6 == null) {
                var6 = var14
            } else if (var6 !== var14) {
                var6.addSuppressed(var14)
            }
            throw var6
        }
    }

    companion object {
        val JSON: MediaType? = MediaType.parse("application/json; charset=utf-8")


        @RequiresApi(Build.VERSION_CODES.O)
        fun enviaPeticion(peticion: String?) {
            val aes = AES256NoPadding()
            val gson = Gson()
            val cliente = ClienteWSECDH()
            var peticionEncriptada: String? = null
            val llaves = getLlaves("idDev")
            if (llaves != null && llaves.size > 0) {
                val secretKey: SecretKeySpec? = aes.getSecretKeySpec(llaves[0], llaves[1])
                peticionEncriptada = peticion?.let { aes.encrypt(it, secretKey) }
                println("Encipt: $peticionEncriptada")
                val men = Mensaje()
                var respuesta: Mensaje? = null
                men.setNoises(peticionEncriptada)
                val response = cliente.post(
                    "http://direccionIp:8080/api/v2/decifraMensaje",
                    "3118AB86-5707-4422-A89B-BCD3F00898AC",
                    gson.toJson(men)
                )
                respuesta = gson.fromJson<Any>(response, Mensaje::class.java) as Mensaje
                if (respuesta != null) {
                    if (respuesta.getNoises() != null) {
                        val respues: String? = aes.decrypt(respuesta.getNoises(), secretKey)
                        println("Respondio: $respues")
                    } else {
                        println("No decifro el mensaje!")
                    }
                } else {
                    println("No tenemos llaves")
                }
            } else {
                println("No respondio")
            }
        }

        fun negociaClaves() {
            val ec = Ecdh()
            val peticion = KeyExchange()
            var respuesta: KeyExchange? = null
            val gson = Gson()
            val cliente = ClienteWSECDH()
            val llaves = Llaves()
            val kp: KeyPair? = ec.getKeys()
            Log.e("Llave:",kp?.public.toString())
            val ourPk: ByteArray = kp?.public!!.getEncoded()
            peticion.setKey(ourPk.toString())
            Log.e("Peticion:",gson.toJson(peticion).toString())
            val response = cliente.post(
                "http://DireccionIp:8080/api/v2/setKey",
                "3118AB86-5707-4422-A89B-BCD3F00898AC",
                gson.toJson(peticion).toString()
            )
            Log.e("Respuesta:",response)
            respuesta = gson.fromJson<Any>(response, KeyExchange::class.java) as KeyExchange
            Log.e("Respuesta:",respuesta.toString())

            if (respuesta.getCod() === 0) {
                val otherPk: ByteArray = DatatypeConverter.parseHexBinary(respuesta.getKey())
                println(
                    "La otra llave publica: " +
                        otherPk.toString()
                    )
                val sharedSC: ByteArray? = ec.getSecretPass(respuesta.getKey(), kp)
                val sharedSCStr: String = DatatypeConverter.printHexBinary(sharedSC)
                println("Shared secret: $sharedSCStr")
                val salt: String? = ec.getHashDerivedKey(sharedSC, ourPk, otherPk)
                println("Final key: $salt")
                llaves.setSecret(sharedSCStr)
                llaves.setHash(salt)
                guardaDatos(llaves)
            } else {
                println("No se pudo calcular!")
            }
        }

        private fun getLlaves(idDev: String): List<String>? {
            var llaves: MutableList<String>? = null
            var archivo: File? = null
            var fr: FileReader? = null
            var br: BufferedReader? = null
            try {
                archivo = File("C:\\Bd\\almacenCel.txt")
                fr = FileReader(archivo)
                br = BufferedReader(fr)
                llaves = ArrayList()
                var linea: String
                while (br.readLine().also { linea = it } != null) {
                    llaves.add(linea)
                }
            } catch (var14: Exception) {
                var14.printStackTrace()
            } finally {
                try {
                    if (fr != null) {
                        fr.close()
                    }
                } catch (var13: Exception) {
                    var13.printStackTrace()
                }
            }
            return llaves
        }


        fun guardaDatos(llaves: Llaves): Boolean {
            val operacion = false
            var fichero: FileWriter? = null
            var pw: PrintWriter? = null
            try {
                fichero = FileWriter("C:\\Bd\\almacenCel.txt")
                pw = PrintWriter(fichero)
                pw.println(llaves.getSecret())
                pw.println(llaves.getHash())
            } catch (var11: Exception) {
                throw var11
            } finally {
                try {
                    if (fichero != null) {
                        fichero.close()
                    }
                } catch (var12: Exception) {
                    var12.printStackTrace()
                    throw var12
                }
            }
            return operacion
        }
    }
}