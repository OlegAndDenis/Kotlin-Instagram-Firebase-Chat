package com.example.kotlininstagramfirebasechat.screens.chats.chat


import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.utils.DateUtils
import com.example.kotlininstagramfirebasechat.utils.loadUserPhoto
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_chat_companion_user.view.*
import kotlinx.android.synthetic.main.item_chat_companion_user_light.view.*
import kotlinx.android.synthetic.main.item_chat_current_user.view.*
import kotlinx.android.synthetic.main.item_chat_current_user_light.view.*

class ChatFromItem(val text: String, private val timestamp: Long) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.run {
            textview_from_row.text = text
            from_msg_time.text = DateUtils.getFormattedTimeChatLog(timestamp)
        }
    }

    override fun getLayout() = R.layout.item_chat_current_user

}

class ChatToItem(val text: String, private val userPhoto: String?, private val timestamp: Long) :
    Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.run {
            textview_to_row.text = text
            to_msg_time.text = DateUtils.getFormattedTimeChatLog(timestamp)
            imageview_chat_to_row.loadUserPhoto(userPhoto)
        }
    }

    override fun getLayout() = R.layout.item_chat_companion_user

}

class ChatFromItemLight(val text: String) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row_light.text = text
    }

    override fun getLayout() = R.layout.item_chat_current_user_light

}

class ChatToItemLight(val text: String) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row_light.text = text
    }

    override fun getLayout() = R.layout.item_chat_companion_user_light
}