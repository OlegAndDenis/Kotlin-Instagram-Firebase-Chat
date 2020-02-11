package com.example.kotlininstagramfirebasechat.screens.chats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlininstagramfirebasechat.models.ChatRow
import com.example.kotlininstagramfirebasechat.models.FeedPost
import com.example.kotlininstagramfirebasechat.models.Message
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.screens.chats.ChatsFragment.Companion.TAG

class ChatsViewModel : ViewModel() {


    private val _chatsRows = MutableLiveData(HashMap<String, ChatRow>())
    val chatsRows: LiveData<HashMap<String, ChatRow>>
        get() = _chatsRows

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    fun updateMessages(key: String, user: User?, message: Message?, isOnline: Boolean) {
        if (user != null && message != null) {
            _chatsRows.value!![key] = ChatRow(user, message, isOnline)
            _chatsRows.notifyObserver()
            Log.d(TAG, "updateMessage $key $message")
        }
    }

}