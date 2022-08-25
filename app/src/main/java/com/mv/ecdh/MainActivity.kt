package com.mv.ecdh

import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mv.ecdh.core.ClienteWSECDH.Companion.negociaClaves



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        task1().execute()

    }
    internal inner class task1 : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg p0: Void?): String? {
            try{
                negociaClaves()
            } catch (var14: Exception) {
                var14.printStackTrace()
            }

            return p0.toString()
        }

    }

}


