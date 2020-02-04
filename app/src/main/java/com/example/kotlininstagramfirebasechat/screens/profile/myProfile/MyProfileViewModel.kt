package com.example.kotlininstagramfirebasechat.screens.profile.myProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlininstagramfirebasechat.models.User

class MyProfileViewModel: ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _images = MutableLiveData<List<String>>()
    val images: LiveData<List<String>>
        get() = _images

    fun updateUser(user: User?) {
        if (user != null) _user.value = user
    }

    fun updateImages(images: List<String>?) {
        if (images != null) _images.value = images
    }

}