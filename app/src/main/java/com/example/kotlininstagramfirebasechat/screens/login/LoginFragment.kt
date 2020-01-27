package com.example.kotlininstagramfirebasechat.screens.login


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.example.kotlininstagramfirebasechat.R
import kotlinx.android.synthetic.main.fragment_login.view.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        view.login_register_text.setOnClickListener { navigateToRegister() }
        return view
    }

    private fun navigateToRegister() {
        findNavController().navigate(LoginFragmentDirections.actionLoginToRegister())
    }


}
