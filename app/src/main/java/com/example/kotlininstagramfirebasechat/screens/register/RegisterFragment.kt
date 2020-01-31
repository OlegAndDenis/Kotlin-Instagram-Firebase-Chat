package com.example.kotlininstagramfirebasechat.screens.register


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.example.kotlininstagramfirebasechat.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var firebase: FirebaseHelper
    private lateinit var user: User

    companion object {
        val TAG = RegisterFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase = FirebaseHelper()
        user = User()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register_sign_up_button.setOnClickListener { performRegistration() }
    }

    private fun performRegistration() {
        val email = register_email_input.text.toString()
        val password = register_password_input.text.toString()
        val name = register_name_input.text.toString()

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            showToast(context, "Please fill all the fields")
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = it.user?.uid ?: "error"
                val ref = firebase.database.child("users").child(uid)

                ref.setValue(User(uid = uid, name = name, email = email))
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully created user with uid: $uid")
                    }
                    .addOnFailureListener { exception ->
                        Log.d(TAG, "Failed to set value to database: ${exception.message}")
                    }

                Log.d(TAG, "Successfully created user with uid: $uid")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to create user: ${it.message}")
                showToast(context, it.message)
            }
    }

}
