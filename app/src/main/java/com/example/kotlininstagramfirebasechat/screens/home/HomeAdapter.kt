package com.example.kotlininstagramfirebasechat.screens.home

import android.view.View
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.HomePost
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.example.kotlininstagramfirebasechat.utils.loadPostImage
import com.example.kotlininstagramfirebasechat.utils.loadUserPhoto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_feed.view.*

class HomeAdapter(private val post: HomePost, val likeClickListener: (HomePost) -> Unit) :
    Item<ViewHolder>() {

    override fun getLayout() = R.layout.item_feed

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.run {

            item_feed_caption.visibility =
                if (post.feedPost.caption.isEmpty()) View.GONE else View.VISIBLE

            item_feed_image.loadPostImage(post.feedPost.image)
            item_feed_photo.loadUserPhoto(post.user.photo)
            item_feed_name.text = post.user.name
            item_feed_caption.text = post.feedPost.caption
            item_feed_like.setOnClickListener { likeClickListener(post) }

            val likesCount = post.feedPost.likes.size

            item_feed_likes_count.text = if (likesCount != 0) {
                 likesCount.toString()
            } else ""

            item_feed_like.setImageResource(
                if (post.feedPost.likes.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {
                    R.drawable.ic_like_active
                } else R.drawable.ic_like
            )
        }
    }


}