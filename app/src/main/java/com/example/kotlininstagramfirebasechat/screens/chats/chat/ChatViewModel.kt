package com.example.kotlininstagramfirebasechat.screens.chats.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlininstagramfirebasechat.models.User

class ChatViewModel : ViewModel() {
    private val _companionUser = MutableLiveData<User>()
    val companionUser: LiveData<User>
        get() = _companionUser

    fun updateCompanionUser(user: User?) {
        if (user != null) {
            _companionUser.value = user
        }
    }
}