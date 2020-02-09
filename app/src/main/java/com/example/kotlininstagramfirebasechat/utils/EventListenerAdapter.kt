package com.example.kotlininstagramfirebasechat.utils

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

private const val V_TAG = "ValueEventListenerAdapt"
private const val C_TAG = "ChildEventListenerAdapt"

class ValueEventListenerAdapter(val handler: (DataSnapshot) -> Unit) : ValueEventListener {

    override fun onDataChange(data: DataSnapshot) {
        handler(data)
    }
    override fun onCancelled(error: DatabaseError) {
        Log.e(V_TAG, "onCancelled: ", error.toException())
    }
}

class ChildEventListenerAdapter(val handler: (DataSnapshot) -> Unit) : ChildEventListener {


    override fun onCancelled(error: DatabaseError) {
        Log.e(C_TAG, "onCancelled: ", error.toException())
    }

    override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

    override fun onChildChanged(data: DataSnapshot, p1: String?) {
        handler(data)
    }

    override fun onChildAdded(data: DataSnapshot, p1: String?) {
        handler(data)
    }

    override fun onChildRemoved(p0: DataSnapshot) {}

}