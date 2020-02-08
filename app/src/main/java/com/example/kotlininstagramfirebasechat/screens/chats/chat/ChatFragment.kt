package com.example.kotlininstagramfirebasechat.screens.chats.chat


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.kotlininstagramfirebasechat.MainActivity
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.Message
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.*
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.progress_bar.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.lang.Exception

class ChatFragment : Fragment(R.layout.fragment_chat), KeyboardVisibilityEventListener {

    companion object {
        val TAG = ChatFragment::class.java.simpleName
    }

    private lateinit var currentUid: String
    private lateinit var firebase: FirebaseHelper
    //    private var companionUser: User? = null
    private val adapter = GroupAdapter<ViewHolder>()
    private val viewModel: ChatViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase = FirebaseHelper(context)
        currentUid = firebase.auth.currentUser!!.uid

        try {
            arguments?.let {
                val uid = ChatFragmentArgs.fromBundle(it).companionUserUid
                getUser(uid)
                isConnected()
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message ?: return)
        }

    }

    private fun getUser(uid: String) {
        firebase.userReference(uid)
            .addValueEventListener(ValueEventListenerAdapter { userData ->
                viewModel.updateCompanionUser(userData.asUser())
                updateActionBarTitle(viewModel.companionUser.value!!.name)
            })
    }

    private fun updateActionBarTitle(title: String) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }

    private fun isConnected() {
        firebase.database.child("connections/${viewModel.companionUser.value!!.uid}")
            .addValueEventListener(ValueEventListenerAdapter { connectedData ->
                if (connectedData.exists()) {
                    updateActionBarSubTitle(getString(R.string.online))
                } else {
                    getLastConnected()
                }
            })
    }

    private fun updateActionBarSubTitle(title: String) {
        (activity as AppCompatActivity).supportActionBar?.subtitle = title
    }

    private fun getLastConnected() {
        firebase.database.child("last-connected/${viewModel.companionUser.value?.uid}")
            .addListenerForSingleValueEvent(ValueEventListenerAdapter { lastConnectedData ->
                if (lastConnectedData.exists()) {
                    val date = DateUtils.getFormattedTimeChatLog(
                        lastConnectedData.getValue(Long::class.java)!! / 1000L
                    )
                    (activity as AppCompatActivity).supportActionBar?.subtitle = "last seen $date"

                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chat_recyclerview.adapter = adapter

        progress_bar.showView()

        KeyboardVisibilityEvent.setEventListener(activity as MainActivity, this)
        coordinateImgBtnAndInputs(chat_send_button, chat_message_input)

        chat_send_button.setOnClickListener { performSendMessage() }

        viewModel.companionUser.observe(viewLifecycleOwner, Observer { listenForMessages(it) })
    }

    private fun listenForMessages(companionUser: User) {
        var sameUser = ""

        firebase.messages(currentUid, companionUser.uid)
            .addChildEventListener(ChildEventListenerAdapter { data ->
                data.asMessage()?.let {
                    if (it.uid == currentUid) {
                        if (it.uid == sameUser) {
                            adapter.add(ChatFromItemLight(it.text))
                        } else {
                            sameUser = it.uid
                            adapter.add(ChatFromItem(it.text, it.timestamp))
                        }
                    } else {
                        if (it.uid == sameUser) {
                            adapter.add(ChatToItemLight(it.text))
                        } else {
                            sameUser = it.uid
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

        val reference = firebase.messages(currentUid, viewModel.companionUser.value!!.uid).push()
        val toReference = firebase.messages(viewModel.companionUser.value!!.uid, currentUid).push()

        val message = Message(currentUid, text, System.currentTimeMillis() / 1000)

        toReference.setValue(message)
        reference.setValue(message)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                chat_message_input.text.clear()
                scrollChatDown()
            }


        firebase.latestMessages(currentUid, viewModel.companionUser.value!!.uid).setValue(message)
        firebase.latestMessages(viewModel.companionUser.value!!.uid, currentUid).setValue(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateActionBarSubTitle("")
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