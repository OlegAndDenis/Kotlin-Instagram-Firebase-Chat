package com.example.kotlininstagramfirebasechat.screens.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.example.kotlininstagramfirebasechat.utils.coordinateBtnAndInputs
import kotlinx.android.synthetic.main.fragment_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment(R.layout.fragment_login), KeyboardVisibilityEventListener {
    private lateinit var firebase: FirebaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebase = FirebaseHelper()

        login_sign_in_button.setOnClickListener { signIn() }
        login_register_text.setOnClickListener { navigateToRegister() }
    }

    private fun signIn() {
        val email = login_email_input.text.toString()
        val password = login_password_input.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            tryToLogin(email, password)
        } else {
            showToast(getString(R.string.login_empty_fields))
        }
    }

    private fun tryToLogin(email: String, password: String) {
        firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {  }
            .addOnFailureListener { showToast(it.message) }
    }

    private fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToRegister() {
        findNavController().navigate(LoginFragmentDirections.actionLoginToRegister())
    }

    override fun onVisibilityChanged(isOpen: Boolean) {
        KeyboardVisibilityEvent.setEventListener(activity, this)
        coordinateBtnAndInputs(login_sign_in_button, login_email_input, login_password_input)
    }


}
