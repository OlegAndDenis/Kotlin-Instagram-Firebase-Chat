package com.example.kotlininstagramfirebasechat.screens.main


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    private lateinit var mFirebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebase = FirebaseHelper()

        mFirebase.auth.addAuthStateListener {
            if (it.currentUser == null) {
                findNavController().navigate(MainFragmentDirections.actionMainToLogin())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


}
