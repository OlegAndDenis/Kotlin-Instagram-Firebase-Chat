package com.example.kotlininstagramfirebasechat.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class FirebaseHelper(val context: Context? = null) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val storage: StorageReference = FirebaseStorage.getInstance().reference


    fun uploadUserPhoto(
        photo: Uri,
        onSuccess: (UploadTask.TaskSnapshot) -> Unit
    ) {
        storage.child("users/${auth.currentUser!!.uid}/photo").putFile(photo)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.result!!)
                } else {
                    showToast(context, it.exception!!.message!!)
                }
            }
    }

    fun updateUserPhoto(
        photoUrl: String,
        onSuccess: () -> Unit
    ) {
        database.child("users/${auth.currentUser!!.uid}/photo").setValue(photoUrl)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    showToast(context, it.exception!!.message!!)
                }
            }
    }

    fun updateUser(
        updates: Map<String, Any?>
    ) {
        database.child("users").child(auth.currentUser!!.uid).updateChildren(updates)
            .addOnFailureListener {
                showToast(context, it.message)
            }
    }

    fun updateEmail(email: String, onSuccess: () -> Unit) {
        auth.currentUser!!.updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                showToast(context, it.exception!!.message!!)
            }
        }
    }

    fun reauthenticate(credential: AuthCredential, onSuccess: () -> Unit) {
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

    fun storageUid() = storage.child("users/${auth.currentUser!!.uid}/photo").downloadUrl

    fun userReference(uid: String = auth.currentUser!!.uid): DatabaseReference = database.child("users/$uid")

    fun messages(fromId: String, toId: String): DatabaseReference =
        database.child("/user-messages/$fromId/$toId")

    fun isFollow(uid: String) = database.child("subscriptions/${auth.currentUser!!.uid}/$uid")

    fun subscription(uid: String) = database.child("subscriptions/${auth.currentUser!!.uid}/$uid")

    fun latestMessages(fromId: String, toId: String): DatabaseReference =
        database.child("/latest-messages/$fromId/$toId")

    fun images(uid: String = auth.currentUser!!.uid) = database.child("/feed-posts/$uid")

    fun storageShare(uri: Uri) =
        storage.child("users/${auth.currentUser!!.uid}/images/${uri.lastPathSegment}").downloadUrl
}