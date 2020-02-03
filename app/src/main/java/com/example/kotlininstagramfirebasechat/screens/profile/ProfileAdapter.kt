package com.example.kotlininstagramfirebasechat.screens.profile

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.FeedPost
import com.example.kotlininstagramfirebasechat.utils.loadImage
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_profile.view.*

//class ProfileAdapter(private val feedPost: FeedPost) : Item<ViewHolder>() {
//
//    override fun bind(viewHolder: ViewHolder, position: Int) {
//        viewHolder.itemView.item_profile_image.loadImage(feedPost.image)
//        Log.d("MyProfile", feedPost.image)
//    }
//
//    override fun getLayout() = R.layout.item_profile
//
//}

class ProfileAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {

    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile, parent, false) as ImageView
        return ViewHolder(image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(images[position])
    }

    override fun getItemCount(): Int = images.size
}