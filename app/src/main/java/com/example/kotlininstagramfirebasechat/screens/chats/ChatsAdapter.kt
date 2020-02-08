package com.example.kotlininstagramfirebasechat.screens.chats


import android.view.View
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.ChatRow
import com.example.kotlininstagramfirebasechat.utils.DateUtils
import com.example.kotlininstagramfirebasechat.utils.loadUserPhoto
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_chats.view.*

class ChatsAdapter(val chatRow: ChatRow) : Item<ViewHolder>() {

    override fun getLayout() = R.layout.item_chats

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.run {
            latest_message_textview.text = chatRow.message.text
            username_textview_latest_message.text = chatRow.user.name
            latest_msg_time.text = DateUtils.getFormattedTime(chatRow.message.timestamp)
            imageview_latest_message.loadUserPhoto(chatRow.user.photo)
            item_chats_online_indicator.visibility =
                if (chatRow.isOnline) View.VISIBLE else View.GONE
        }
    }

}