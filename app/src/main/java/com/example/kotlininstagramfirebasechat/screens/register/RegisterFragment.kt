package com.example.kotlininstagramfirebasechat.screens.register


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A simple [Fragment] subclass.
 */
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
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                val uid = it.result?.user?.uid ?: "error"
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
                Toast.makeText(context, "${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}
