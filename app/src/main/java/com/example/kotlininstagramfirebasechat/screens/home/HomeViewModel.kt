package com.example.kotlininstagramfirebasechat.screens.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlininstagramfirebasechat.models.FeedPost
import com.example.kotlininstagramfirebasechat.screens.home.HomeFragment.Companion.TAG

class HomeViewModel : ViewModel() {
    private val _subscriptions = MutableLiveData<List<String>>()
    val subscriptions: LiveData<List<String>>
        get() = _subscriptions

    private val _posts = MutableLiveData<MutableList<List<FeedPost>>>()
    val posts: LiveData<MutableList<List<FeedPost>>>
        get() = _posts

    fun updatePosts(posts: List<FeedPost>) {
        Log.d(TAG, "viewModel ${posts.size}")

        _posts.value!!.plusAssign(posts)
        _posts.notifyObserver()
    }

    fun updateSubscriptions(list: List<String>) {
        _subscriptions.value = list
    }

    fun clearPosts() {
        _posts.value = mutableListOf()
    }

    fun clearSubscriptions() {
        _subscriptions.value = listOf()
    }

    init {
        _posts.value = mutableListOf()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }

    operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
        val value = this.value ?: arrayListOf()
        value.addAll(values)
        this.value = value
    }
}