package com.example.kotlininstagramfirebasechat.screens.profile


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var uid: String

    companion object {
        val TAG = ProfileFragment::class.java.simpleName
    }

    private lateinit var firebase: FirebaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebase = FirebaseHelper()

        arguments?.let {
            uid = ProfileFragmentArgs.fromBundle(it).UserUid

            firebase.userReference(uid).addListenerForSingleValueEvent(object: ValueEventListener {

                override fun onDataChange(data: DataSnapshot) {
                    val user = data.getValue(User::class.java)
                    profile_name.text = user?.name
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ", error.toException())
                }

            })

            profile_chat_button.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileToChat(uid))
            }
        }


    }
}
