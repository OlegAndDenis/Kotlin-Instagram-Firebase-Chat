package com.example.kotlininstagramfirebasechat.screens.profile.editProfile


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.*
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var newUser = User()
    private lateinit var firebase: FirebaseHelper
    private lateinit var camera: CameraHelper

    companion object {
        val TAG = EditProfileFragment::class.java.simpleName
    }

    private val viewModel: EditProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        firebase = FirebaseHelper(context)
        camera = CameraHelper(this)

        firebase.userReference().addValueEventListener(object:
            ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ", error.toException())
            }

            override fun onDataChange(data: DataSnapshot) {
                viewModel.updateUser(data.getValue(User::class.java) ?: return)
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner, Observer {
            updateView(it)
        })

        viewModel.password.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "password: $it")
            onPasswordConfirm(it)
        })

        edit_profile_image.setOnClickListener { camera.takeCameraPicture() }

        edit_profile_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult")
        if (requestCode == REQUEST_GALLERY_PICTURE && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult Result Ok")
            firebase.uploadUserPhoto(data?.data ?: return) {
                firebase.storageUid().addOnCompleteListener {
                    val photoUrl = it.result.toString()
                    firebase.updateUserPhoto(photoUrl) {}
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.update_user, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.update_user) updateUser()
        return super.onOptionsItemSelected(item)
    }

    private fun updateUser() {
        newUser = readInputs()
        val error = validate(newUser)
        if (error == null) {
            if (newUser.email == viewModel.user.value?.email) {
                updateUser2()
            } else {
                findNavController().navigate(EditProfileFragmentDirections.actionEditProfileToEmailChangeDialog())
            }
        } else {
            showToast(context, error)
        }
    }

    private fun updateUser2() {
        val updatesMap = viewModel.updateUserData(newUser)

        firebase.updateUser(updatesMap)
            showToast(context,"Profile saved")

    }

    private fun validate(user: User): String? =
        when {
            user.name.isEmpty() -> "Please enter your name"
            else -> null
        }

    private fun readInputs(): User {
        return User(
            name = edit_profile_name_input.text.toString(),
            email = edit_profile_email_input.text.toString(),
            bio = edit_profile_bio_input.text!!.toStringOrNull()
        )
    }

    private fun updateView(user: User) {
        edit_profile_name_input.setText(user.name)
        edit_profile_email_input.setText(user.email)
        edit_profile_bio_input.setText(user.bio)
        edit_profile_image.loadUserPhoto(user.photo)
    }

    private fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(viewModel.user.value!!.email, password)
            Log.d(TAG, "email: ${viewModel.user.value!!.email}")
            firebase.reauthenticate(credential) {
                firebase.updateEmail(newUser.email) {
                    updateUser2()
                }
            }
            viewModel.setPassword("")
        } else {
            showToast(context, "Enter your password")
        }
    }
}
