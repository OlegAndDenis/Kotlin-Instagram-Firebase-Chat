package com.example.kotlininstagramfirebasechat.screens.chats


import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.ChatMessage
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.DateUtils
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.example.kotlininstagramfirebasechat.utils.loadUserPhoto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_chats.view.*

class ChatsAdapter(val chatMessage: ChatMessage) : Item<ViewHolder>() {

    private val firabase = FirebaseHelper()
    var chatPartnerUser: User? = null

    override fun getLayout() = R.layout.item_chats

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latest_message_textview.text = chatMessage.text

        val chatPartnerId =
            if (chatMessage.fromId == firabase.auth.uid) chatMessage.toId else chatMessage.fromId

        val ref = firabase.userReference(chatPartnerId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(data: DataSnapshot) {
                chatPartnerUser = data.getValue(User::class.java)
                viewHolder.itemView.run {
                    username_textview_latest_message.text = chatPartnerUser?.name
                    latest_msg_time.text = DateUtils.getFormattedTime(chatMessage.timestamp)
                    imageview_latest_message.loadUserPhoto(chatPartnerUser?.photo)
                }
            }

        })
    }

}