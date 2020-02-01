package com.example.kotlininstagramfirebasechat.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun updateUser(
        updates: Map<String, Any?>,
        context: Context?
    ) {
        database.child("users").child(auth.currentUser!!.uid).updateChildren(updates)
            .addOnFailureListener {
                showToast(context, it.message)
            }
    }

    fun updateEmail(email: String, context: Context?, onSuccess: () -> Unit) {
        auth.currentUser!!.updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(context, it.exception!!.message!!)
            }
        }
    }

    fun reauthenticate(credential: AuthCredential, context: Context?, onSuccess: () -> Unit) {
        auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("Edit", "success")
                onSuccess()
            } else {
                showToast(context, it.exception!!.message!!)
                Log.d("Edit", it.exception!!.message!!)
            }
        }
    }

    fun currentUserReference(): DatabaseReference = database.child("users").child(auth.currentUser!!.uid)
    fun userReference(uid: String): DatabaseReference = database.child("users/$uid")
    fun messages(fromId: String, toId: String): DatabaseReference = database.child("/user-messages/$fromId/$toId")
    fun latestMessages(fromId: String, toId: String): DatabaseReference = database.child("/latest-messages/$fromId/$toId")
}