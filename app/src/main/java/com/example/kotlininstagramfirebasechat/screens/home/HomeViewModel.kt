package com.example.kotlininstagramfirebasechat.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlininstagramfirebasechat.models.FeedPost
import com.example.kotlininstagramfirebasechat.models.HomePost
import com.example.kotlininstagramfirebasechat.models.User

class HomeViewModel : ViewModel() {
    private val _subscriptions = MutableLiveData<List<String>>()
    val subscriptions: LiveData<List<String>>
        get() = _subscriptions

    private val _posts = MutableLiveData<HashMap<String, HomePost>>()
    val posts: LiveData<HashMap<String, HomePost>>
        get() = _posts

    fun updatePosts(post: FeedPost, key: String, user: User?) {
        if (user != null) {
            _posts.value!![key] = HomePost(post, user, key)
            _posts.notifyObserver()
        }
    }

    fun updateSubscriptions(list: List<String>) {
        _subscriptions.value = list
    }

    init {
        _posts.value = HashMap()
        _subscriptions.value = listOf()
    }

    fun clearPosts() {
        _posts.value = HashMap()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

//    operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
//        val value = this.value ?: arrayListOf()
//        value.addAll(values)
//        this.value = value
//    }
}