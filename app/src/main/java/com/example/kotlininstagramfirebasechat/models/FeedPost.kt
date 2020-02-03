package com.example.kotlininstagramfirebasechat.models

import com.google.firebase.database.ServerValue
import java.util.*

data class FeedPost(
    val uid: String = "",
    val image: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val caption: String = "",
    val comments: List<Comment> = emptyList(),
    val timestamp: Any = ServerValue.TIMESTAMP
) {
    fun timestampDate(): Date = Date(timestamp as Long)
}