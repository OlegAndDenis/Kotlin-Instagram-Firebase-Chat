package com.example.kotlininstagramfirebasechat.screens.chats


import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.ChatRow
import com.example.kotlininstagramfirebasechat.utils.DateUtils
import com.example.kotlininstagramfirebasechat.utils.loadUserPhoto
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_chats.view.*

class ChatsAdapter(val chatMessage: ChatRow) : Item<ViewHolder>() {

    override fun getLayout() = R.layout.item_chats

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.run {
            latest_message_textview.text = chatMessage.message.text
            username_textview_latest_message.text = chatMessage.user.name
            latest_msg_time.text = DateUtils.getFormattedTime(chatMessage.message.timestamp)
            imageview_latest_message.loadUserPhoto(chatMessage.user.photo)
        }
    }

}