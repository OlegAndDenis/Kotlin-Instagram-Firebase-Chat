package com.example.kotlininstagramfirebasechat

import android.app.Application
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.example.kotlininstagramfirebasechat.utils.ValueEventListenerAdapter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class Peselgram : Application() {
    private lateinit var firebase: FirebaseHelper

    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        firebase = FirebaseHelper(baseContext)

        manageConnections()
    }

    private fun manageConnections() {
        val connectionReference = firebase.database.child("connections")
        val lastConnected = firebase.database
            .child("last-connected/${firebase.auth.currentUser?.uid}")
        val infoConnected = firebase.database.child(".info/connected")

        infoConnected.addValueEventListener(ValueEventListenerAdapter { data ->
            data.getValue(Boolean::class.java).let {
                if (it!!) {
                    val con = connectionReference.child(
                        firebase.auth.currentUser?.uid ?: return@ValueEventListenerAdapter
                    )
                    con.setValue(java.lang.Boolean.TRUE)
                    con.onDisconnect().removeValue()
                    lastConnected.onDisconnect().setValue(ServerValue.TIMESTAMP)
                }
            }


        })
    }

}