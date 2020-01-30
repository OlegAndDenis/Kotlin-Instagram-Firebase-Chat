package com.example.kotlininstagramfirebasechat.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference


    fun currentUserReference(): DatabaseReference = database.child("users").child(auth.currentUser!!.uid)
    fun UserReference(uid: String): DatabaseReference = database.child("users").child(uid)
}