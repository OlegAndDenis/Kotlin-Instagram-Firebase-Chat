package com.example.kotlininstagramfirebasechat.screens.chats.chat


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import com.example.kotlininstagramfirebasechat.MainActivity
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.ChatMessage
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.*
import com.example.kotlininstagramfirebasechat.utils.DateUtils.getFormattedTimeChatLog
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.item_chat_current_user.view.*
import kotlinx.android.synthetic.main.item_chat_companion_user.view.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.item_chat_companion_user_light.view.*
import kotlinx.android.synthetic.main.item_chat_current_user_light.view.*
import kotlinx.android.synthetic.main.progress_bar.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.lang.Exception

class ChatFragment : Fragment(R.layout.fragment_chat), KeyboardVisibilityEventListener {

    companion object {
        val TAG = ChatFragment::class.java.simpleName
    }

    private var companionUser = User()
    private lateinit var currentUid: String
    private lateinit var firebase: FirebaseHelper

    private val adapter = GroupAdapter<ViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_bar.showView()

        KeyboardVisibilityEvent.setEventListener(activity as MainActivity, this)
        coordinateImgBtnAndInputs(chat_send_button, chat_message_input)

        firebase = FirebaseHelper(context)

        currentUid = firebase.auth.currentUser!!.uid

        arguments?.let {
            val uid = ChatFragmentArgs.fromBundle(it).companionUserUid
            firebase.userReference(uid).addValueEventListener(ValueEventListenerAdapter { data ->
                companionUser = data.getValue(User::class.java) ?: User()
                listenForMessages()
            })
        }

        chat_recyclerview.adapter = adapter

        chat_send_button.setOnClickListener { performSendMessage() }
    }

    private fun listenForMessages() {
        var sameUser = ""

        firebase.messages(currentUid, companionUser.uid).addChildEventListener(ChildEventListenerAdapter { data ->
            data.getValue(ChatMessage::class.java)?.let {
                if (it.fromId == currentUid) {
                    if (it.fromId == sameUser) {
                        adapter.add(ChatFromItemLight(it.text))
                    } else {
                        sameUser = it.fromId
                        adapter.add(ChatFromItem(it.text, it.timestamp))
                    }
                } else {
                    if (it.fromId == sameUser) {
                        adapter.add(ChatToItemLight(it.text))
                    } else {
                        sameUser = it.fromId
                        adapter.add(ChatToItem(it.text, companionUser.photo, it.timestamp))
                    }
                }
            }
            scrollChatDown()
        })

        progress_bar.hideView()
    }

    private fun performSendMessage() {
        val text = chat_message_input.text.toString()

        if (text.isEmpty()) return

        val reference = firebase.messages(currentUid, companionUser.uid).push()
        val toReference = firebase.messages(companionUser.uid, currentUid).push()

        val chatMessage =
            ChatMessage(reference.key!!, text, currentUid, companionUser.uid, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                chat_message_input.text.clear()
                scrollChatDown()
            }

        toReference.setValue(chatMessage)

        firebase.latestMessages(currentUid, companionUser.uid).setValue(chatMessage)
        firebase.latestMessages(companionUser.uid, currentUid).setValue(chatMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideKeyboard(activity as MainActivity)
    }

    override fun onVisibilityChanged(isOpen: Boolean) = scrollChatDown()

    private fun scrollChatDown() {
        try {
            chat_recyclerview.scrollToPosition(adapter.itemCount - 1)
        } catch (e: Exception) {
            Log.d(TAG, e.message ?: return)
        }
    }

}