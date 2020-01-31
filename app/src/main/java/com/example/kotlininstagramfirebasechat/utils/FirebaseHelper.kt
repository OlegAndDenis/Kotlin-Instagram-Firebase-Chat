package com.example.kotlininstagramfirebasechat.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun currentUserReference(): DatabaseReference = database.child("users").child(auth.currentUser!!.uid)
    fun userReference(uid: String): DatabaseReference = database.child("users/$uid")
    fun messages(fromId: String, toId: String): DatabaseReference = database.child("/user-messages/$fromId/$toId")
    fun latestMessages(fromId: String, toId: String): DatabaseReference = database.child("/latest-messages/$fromId/$toId")
}