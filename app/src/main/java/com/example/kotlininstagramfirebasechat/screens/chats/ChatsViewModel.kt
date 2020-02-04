package com.example.kotlininstagramfirebasechat.screens.chats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlininstagramfirebasechat.models.ChatMessage
import com.example.kotlininstagramfirebasechat.screens.chats.ChatsFragment.Companion.TAG

class ChatsViewModel : ViewModel() {

    private val _messages = MutableLiveData<HashMap<String, ChatMessage>>()
    val messages: LiveData<HashMap<String, ChatMessage>>
        get() = _messages

    init {
        _messages.value = HashMap()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    fun updateMessages(key: String, message: ChatMessage) {
        _messages.value!![key] = message
        _messages.notifyObserver()
        Log.d(TAG, "updateMessage $key $message")
    }

}