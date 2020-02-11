package com.example.kotlininstagramfirebasechat.models

import com.google.firebase.database.ServerValue
import java.util.*
import kotlin.collections.HashMap

data class FeedPost(
    val uid: String = "",
    val image: String = "",
    val caption: String = "",
    val likes: HashMap<String,Boolean> = HashMap(),
    val comments: List<Comment> = emptyList(),
    val timestamp: Any = ServerValue.TIMESTAMP
) {
    fun timestampDate(): Date = Date(timestamp as Long)
}