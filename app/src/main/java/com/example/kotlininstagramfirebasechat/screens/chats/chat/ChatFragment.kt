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

    private lateinit var listener: ValueEventListener
    private lateinit var currentUid: String
    private lateinit var firebase: FirebaseHelper
    private val adapter = GroupAdapter<ViewHolder>()
    private val viewModel: ChatViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebase = FirebaseHelper(context)
        currentUid = firebase.auth.currentUser!!.uid
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_bar.showView()

        arguments?.let {
            val uid = ChatFragmentArgs.fromBundle(it).companionUserUid
            getUser(uid)
            isConnected(uid)
        }

        chat_recyclerview.adapter = adapter

        KeyboardVisibilityEvent.setEventListener(activity as MainActivity, this)
        coordinateImgBtnAndInputs(chat_send_button, chat_message_input)

        chat_send_button.setOnClickListener {
            sendMessage()
        }

        viewModel.companionUser.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                listenForMessages(it)
            }
        })
    }

    private fun getUser(uid: String) {
        firebase.userReference(uid)
            .addValueEventListener(ValueEventListenerAdapter { userData ->
                viewModel.updateCompanionUser(userData.asUser())
                updateActionBarTitle(viewModel.companionUser.value!!.name)
            })
    }

    private fun isConnected(uid: String) {
        listener = firebase.isConnected(uid)
            .addValueEventListener(ValueEventListenerAdapter { connectedData ->
                if (connectedData.exists()) {
                    updateActionBarSubTitle(getString(R.string.online))
                } else {
                    getLastConnected(uid)
                }
                progress_bar.hideView()
            })
    }

    private fun sendMessage() {
        val text = chat_message_input.text.toString()
        val currentRef = firebase.messages(currentUid, viewModel.companionUser.value!!.uid).push()
        val companionRef = firebase.messages(viewModel.companionUser.value!!.uid, currentUid).push()
        val message = Message(currentUid, text, System.currentTimeMillis())

        companionRef.setValue(message)
        currentRef.setValue(message)
            .addOnSuccessListener {
                chat_message_input.text.clear()
                scrollChatDown()
            }

        firebase.latestMessages(currentUid, viewModel.companionUser.value!!.uid).setValue(message)
        firebase.latestMessages(viewModel.companionUser.value!!.uid, currentUid).setValue(message)
    }

    private fun listenForMessages(companionUser: User) {
        var sameUser = ""

        firebase.messages(currentUid, companionUser.uid)
            .addChildEventListener(ChildEventListenerAdapter { data ->
                data.asMessage()?.let {
                    adapter.add(
                        when {
                            it.uid == sameUser && it.uid == currentUid -> CurrentUserItemLight(it.text)
                            it.uid == currentUid -> CurrentUserItem(it.text, it.timestamp)
                            it.uid == sameUser -> CompanionUserItemLight(it.text)
                            else -> CompanionUserItem(it.text, companionUser.photo, it.timestamp)
                        }
                    )

                    if (it.uid != sameUser) sameUser = it.uid
                }
                scrollChatDown()
            })
    }

    private fun getLastConnected(uid: String) {
        firebase.lastConnected(uid)
            .addListenerForSingleValueEvent(ValueEventListenerAdapter { lastConnectedData ->
                if (lastConnectedData.exists()) {
                    val date = DateUtils.getFormattedTimeChatLog(
                        lastConnectedData.getValue(Long::class.java)!!
                    )
                    (activity as AppCompatActivity).supportActionBar?.subtitle = "last seen $date"
                }
            })
    }

    private fun updateActionBarTitle(title: String) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }

    private fun updateActionBarSubTitle(title: String) {
        (activity as AppCompatActivity).supportActionBar?.subtitle = title
    }

    override fun onVisibilityChanged(isOpen: Boolean) = scrollChatDown()

    private fun scrollChatDown() {
        try {
            chat_recyclerview.scrollToPosition(adapter.itemCount - 1)
        } catch (e: Exception) {
            Log.d(TAG, e.message ?: return)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firebase.isConnected(viewModel.companionUser.value?.uid ?: "").removeEventListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateActionBarSubTitle("")
        hideKeyboard(activity as MainActivity)
        viewModel.clearUser()
    }

}