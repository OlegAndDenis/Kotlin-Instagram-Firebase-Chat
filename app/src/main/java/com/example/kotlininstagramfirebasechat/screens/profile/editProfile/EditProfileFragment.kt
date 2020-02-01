package com.example.kotlininstagramfirebasechat.screens.profile.editProfile


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.showToast
import com.example.kotlininstagramfirebasechat.utils.toStringOrNull
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var newUser = User()

    companion object {
        val TAG = EditProfileFragment::class.java.simpleName
    }

    private lateinit var viewModel: EditProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        viewModel.firebase.currentUserReference().addListenerForSingleValueEvent(object:
            ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {
                viewModel.updateUser(data.getValue(User::class.java) ?: return)
                Log.d(TAG, "update")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ", error.toException())
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.update_user, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        updateUser()
        return super.onOptionsItemSelected(item)
    }

    private fun updateUser() {
        newUser = readInputs()
        val error = validate(newUser)
        if (error == null) {
            updateUser2()
        } else {
            showToast(context, error)
        }
    }

    private fun updateUser2() {
        val updatesMap = viewModel.updateUserData(newUser)

        viewModel.firebase.updateUser(updatesMap, context)
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
            bio = edit_profile_bio_input.text?.toStringOrNull()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.user.observe(viewLifecycleOwner, Observer {
            updateView(it)
        })
    }

    private fun updateView(user: User) {
        edit_profile_name_input.setText(user.name)
        edit_profile_email_input.setText(user.email)
        edit_profile_bio_input.setText(user.bio)
    }
}
