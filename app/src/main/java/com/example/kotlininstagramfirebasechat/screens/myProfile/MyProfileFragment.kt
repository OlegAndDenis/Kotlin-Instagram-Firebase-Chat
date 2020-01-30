package com.example.kotlininstagramfirebasechat.screens.myProfile


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_my_profile.*

class MyProfileFragment : Fragment(R.layout.fragment_my_profile) {

    companion object {
        val TAG = MyProfileFragment::class.java.simpleName
    }

    private lateinit var firebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        firebase = FirebaseHelper()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebase.currentUserReference().addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {
                val user = data.getValue(User::class.java)
                my_profile_name.text = user?.name
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ", error.toException())
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.sign_out, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        firebase.auth.signOut()
        return true
    }
}

