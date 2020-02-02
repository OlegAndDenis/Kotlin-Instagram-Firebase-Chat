package com.example.kotlininstagramfirebasechat.screens.chats


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.ChatMessage
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.example.kotlininstagramfirebasechat.utils.hideView
import com.example.kotlininstagramfirebasechat.utils.showView
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.progress_bar.*
import java.lang.Exception

class ChatsFragment : Fragment(R.layout.fragment_chats) {

    companion object {
        val TAG = ChatsFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    private lateinit var firebase: FirebaseHelper
    private val adapter = GroupAdapter<ViewHolder>()
    private val latestMessagesMap = HashMap<String, ChatMessage>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        firebase = FirebaseHelper(context)

        recyclerview_latest_messages.adapter = adapter

        listenForLatestMessages()

        adapter.setOnItemClickListener { item, _ ->
            val row = item as ChatsAdapter
            findNavController().navigate(
                ChatsFragmentDirections.actionChatsToChat(
                    row.chatPartnerUser?.uid ?: return@setOnItemClickListener
                )
            )
        }

    }

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(ChatsAdapter(it))
        }
        try {
            progress_bar.hideView()
        } catch (e: Exception) {
            Log.d(TAG, e.message ?: return)
        }
    }

    private fun listenForLatestMessages() {
        val fromId = firebase.auth.uid ?: return
        val ref = firebase.latestMessages(fromId, "")

        progress_bar.showView()

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "database error: " + databaseError.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "has children: " + dataSnapshot.hasChildren())
            }

        })

        ref.addChildEventListener(object : ChildEventListener {

            override fun onCancelled(databaseError: DatabaseError) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                lastMessageChanged(dataSnapshot)
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                lastMessageChanged(dataSnapshot)
            }

            override fun onChildRemoved(p0: DataSnapshot) {}

            private fun lastMessageChanged(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "latestMesssage call")
                dataSnapshot.getValue(ChatMessage::class.java)?.let {
                    latestMessagesMap[dataSnapshot.key!!] = it
                    refreshRecyclerViewMessages()
                }
            }

        })
    }

}
