package com.example.kotlininstagramfirebasechat.screens.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.utils.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.progress_bar.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var firebase: FirebaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coordinateBtnAndInputs(login_sign_in_button, login_email_input, login_password_input)

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
            showToast(context, getString(R.string.login_empty_fields))
        }
    }

    private fun tryToLogin(email: String, password: String) {
        progress_bar.showView()
        firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener {
                showToast(context, it.message)
            }
            .addOnCompleteListener { progress_bar.hideView() }
    }

    private fun navigateToRegister() {
        findNavController().navigate(LoginFragmentDirections.actionLoginToRegister())
    }

}
