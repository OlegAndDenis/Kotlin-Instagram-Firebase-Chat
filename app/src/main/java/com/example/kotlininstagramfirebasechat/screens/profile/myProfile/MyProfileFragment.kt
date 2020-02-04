package com.example.kotlininstagramfirebasechat.screens.profile.myProfile


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.screens.profile.ProfileAdapter
import com.example.kotlininstagramfirebasechat.utils.*
import kotlinx.android.synthetic.main.fragment_my_profile.*

class MyProfileFragment : Fragment(R.layout.fragment_my_profile) {

    companion object {
        val TAG = MyProfileFragment::class.java.simpleName
    }

    private lateinit var firebase: FirebaseHelper
    private val viewModel: MyProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        firebase = FirebaseHelper(context)

        getUserData()
        getUserImage()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner, Observer { updateView(it) })

        viewModel.images.observe(viewLifecycleOwner, Observer { updateImages(it) })

        my_profile_to_edit_profile_button.setOnClickListener {
            findNavController().navigate(MyProfileFragmentDirections.actionMyProfileToEditProfile())
        }
    }

    private fun updateImages(images: List<String>) {
        my_profile_recycler.adapter = ProfileAdapter(images)
    }

    private fun updateView(user: User) {
        my_profile_name.text = user.name
        my_profile_bio.text = user.bio
        my_profile_photo.loadUserPhoto(user.photo)
    }

    private fun getUserData() {
        firebase.userReference().addValueEventListener(ValueEventListenerAdapter { data ->
            viewModel.updateUser(data.asUser())
        })
    }

    private fun getUserImage() {
        val ref = firebase.images()
        ref.addValueEventListener(ValueEventListenerAdapter { data ->
            viewModel.updateImages(data.children.map { it.asFeedPost()!!.image })
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


