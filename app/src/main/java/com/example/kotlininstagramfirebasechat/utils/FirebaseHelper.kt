package com.example.kotlininstagramfirebasechat.utils

import com.google.firebase.auth.FirebaseAuth

class FirebaseHelper {
    val auth: FirebaseAuth =
        FirebaseAuth.getInstance()
}