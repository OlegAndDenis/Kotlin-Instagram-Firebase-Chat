package com.example.kotlininstagramfirebasechat.screens.profile


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.FeedPost
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    companion object {
        val TAG = ProfileFragment::class.java.simpleName
    }

    private lateinit var firebase: FirebaseHelper
    private lateinit var uid: String
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebase = FirebaseHelper(context)

        arguments?.let {
            uid = ProfileFragmentArgs.fromBundle(it).UserUid
        }

        isFollow()
        getUserData()
        getUserImage()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_chat_button.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileToChat(uid))
        }

        profile_follow_button.setOnClickListener { follow() }

        viewModel.isFollow.observe(viewLifecycleOwner, Observer { updateButton(it) })

        viewModel.user.observe(viewLifecycleOwner, Observer { updateView(it) })

        viewModel.images.observe(viewLifecycleOwner, Observer { updateImages(it) })
    }

    private fun updateImages(images: List<String>) {
        my_profile_recycler.adapter = ProfileAdapter(images)
    }

    private fun updateView(user: User) {
        profile_name.text = user.name
        profile_bio.text = user.bio
        profile_photo.loadUserPhoto(user.photo)
    }

    private fun updateButton(value: Boolean) {
        profile_follow_button.text =
            if (value) getString(R.string.unfollow) else getString(R.string.follow)
    }

    private fun follow() {
        if (viewModel.isFollow.value!!) {
            firebase.subscription(uid).removeValue()
        } else {
            firebase.subscription(uid).setValue("true")
        }
    }

    private fun isFollow() {
        firebase.isFollow(uid).addValueEventListener(ValueEventListenerAdapter { data ->
            viewModel.updateIsFollow(data.exists())
        })
    }

    private fun getUserData() {
        firebase.userReference(uid).addValueEventListener(ValueEventListenerAdapter { data ->
            viewModel.updateUser(data.asUser())
        })
    }

    private fun getUserImage() {
        val ref = firebase.images(uid)
        ref.addValueEventListener(ValueEventListenerAdapter { data ->
            viewModel.updateImages(data.children.map { it.asFeedPost()!!.image })
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clean()
    }
}
