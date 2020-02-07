package com.example.kotlininstagramfirebasechat

import android.app.Application
import com.example.kotlininstagramfirebasechat.utils.ValueEventListenerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class Peselgram : Application() {

    private lateinit var db: DatabaseReference

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        db = FirebaseDatabase.getInstance().reference

        manageConnections()
    }

    private fun manageConnections() {
        val connectionReference = db.child("connections")
        val lastConnected = db.child("lastConnected/${FirebaseAuth.getInstance().currentUser!!.uid}")
        val infoConnected = db.child(".info/connected")

        infoConnected.addValueEventListener(ValueEventListenerAdapter {data ->
            data.getValue(Boolean::class.java).let {
                if (it!!) {
                    val con = connectionReference.child(FirebaseAuth.getInstance().currentUser!!.uid)
                    con.setValue(java.lang.Boolean.TRUE)
                    con.onDisconnect().setValue(java.lang.Boolean.FALSE)
                    lastConnected.onDisconnect().setValue(ServerValue.TIMESTAMP)
                }
            }


        })
    }
}