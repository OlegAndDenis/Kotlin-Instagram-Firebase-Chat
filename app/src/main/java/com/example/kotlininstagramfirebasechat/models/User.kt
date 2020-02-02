package com.example.kotlininstagramfirebasechat.models

import com.google.firebase.database.Exclude

class User(
    @Exclude val uid: String = "",
    val name: String = "",
    val email: String = "",
    val bio: String? = null,
    val photo: String? = null
)