package com.example.kotlininstagramfirebasechat.models

data class HomePost(
    val feedPost: FeedPost = FeedPost(),
    val user: User = User()
)