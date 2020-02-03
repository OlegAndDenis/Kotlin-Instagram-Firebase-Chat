package com.example.kotlininstagramfirebasechat.screens.profile


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.FeedPost
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.example.kotlininstagramfirebasechat.utils.ValueEventListenerAdapter
import com.example.kotlininstagramfirebasechat.utils.loadUserPhoto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    companion object {
        val TAG = ProfileFragment::class.java.simpleName
    }

    private lateinit var firebase: FirebaseHelper
    private lateinit var uid: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebase = FirebaseHelper(context)

        arguments?.let {
            uid = ProfileFragmentArgs.fromBundle(it).UserUid

            getUserData()
            getUserImage()

            profile_chat_button.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileToChat(uid))
            }
        }

    }

    private fun getUserData() {
        firebase.userReference(uid).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {
                val user = data.getValue(User::class.java)
                try {
                    profile_name.text = user?.name
                    profile_bio.text = user?.bio
                    profile_photo.loadUserPhoto(user?.photo)
                } catch (e: Exception) {
                    Log.d(TAG, e.message ?: return)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ", error.toException())
            }

        })
    }

    private fun getUserImage() {
        val ref = firebase.images(uid)
        ref.addValueEventListener(ValueEventListenerAdapter {
            val images =
                it.children.map { data ->
                    data.getValue(FeedPost::class.java)!!
                }.map { feedPost ->
                    feedPost.image
                }
            my_profile_recycler.adapter = ProfileAdapter(images)
        })
    }

}
