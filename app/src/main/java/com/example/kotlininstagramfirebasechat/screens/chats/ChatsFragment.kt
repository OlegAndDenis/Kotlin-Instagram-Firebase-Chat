package com.example.kotlininstagramfirebasechat.screens.chats


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.utils.*
import com.google.firebase.database.DataSnapshot
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.progress_bar.*

class ChatsFragment : Fragment(R.layout.fragment_chats) {

    companion object {
        val TAG = ChatsFragment::class.java.simpleName
    }

    private lateinit var firebase: FirebaseHelper
    private val adapter = GroupAdapter<ViewHolder>()
    private val viewModel: ChatsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

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

        viewModel.messages.observe(viewLifecycleOwner, Observer { refreshRecyclerView() })

    }

    private fun refreshRecyclerView() {
        adapter.clear()
        viewModel.messages.value?.values?.forEach {
            adapter.add(ChatsAdapter(it))
            Log.d(TAG, "${it.text} adapter")
        }
    }

    private fun listenForLatestMessages() {
        val fromId = firebase.auth.uid ?: return
        val ref = firebase.latestMessages(fromId, "")
        ref.addChildEventListener(ChildEventListenerAdapter { data ->
            Log.d(TAG, "latestMesssage call")
            data.asChatMessage()?.let {
                viewModel.updateMessages(data.key!!, it)
            }
        })
    }

}
