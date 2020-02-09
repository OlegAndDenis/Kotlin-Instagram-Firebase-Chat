package com.example.kotlininstagramfirebasechat.screens.home

import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.FeedPost
import com.example.kotlininstagramfirebasechat.utils.loadImage
import com.example.kotlininstagramfirebasechat.utils.loadPostImage
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_feed.view.*

class HomeAdapter(private val post: FeedPost) : Item<ViewHolder>() {

    override fun getLayout() = R.layout.item_feed

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.run {
            item_feed_image.loadPostImage(post.image)
        }
    }

}