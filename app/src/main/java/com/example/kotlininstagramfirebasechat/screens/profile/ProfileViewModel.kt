package com.example.kotlininstagramfirebasechat.screens.profile


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlininstagramfirebasechat.models.User

class ProfileViewModel : ViewModel() {

    private val _isFollow = MutableLiveData<Boolean>()
    val isFollow: LiveData<Boolean>
        get() = _isFollow

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _images = MutableLiveData<List<String>>()
    val images: LiveData<List<String>>
        get() = _images

    fun updateIsFollow(value: Boolean) {
        _isFollow.value = value
    }

    fun updateUser(user: User?) {
        if (user != null) _user.value = user
    }

    fun updateImages(images: List<String>?) {
        if (images != null) _images.value = images
    }

    fun clean() {
        _images.value = emptyList()
        _user.value = User()
    }

}