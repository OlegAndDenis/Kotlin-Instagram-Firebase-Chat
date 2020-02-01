package com.example.kotlininstagramfirebasechat.screens.profile.editProfile


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlininstagramfirebasechat.models.User

import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper


class EditProfileViewModel : ViewModel() {
    val firebase = FirebaseHelper()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    init {
        _user.value = User()
    }

    fun updateUser(user: User) {
        _user.value = user
    }

    fun updateUserData(newUser: User): MutableMap<String, Any?> {
        val updatesMap = mutableMapOf<String, Any?>()
        if (newUser.name != user.value!!.name) updatesMap["name"] = newUser.name
        if (newUser.bio != user.value!!.bio) updatesMap["bio"] = newUser.bio
        return updatesMap
    }
}