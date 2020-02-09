package com.example.kotlininstagramfirebasechat.screens.home


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.FeedPost
import com.example.kotlininstagramfirebasechat.models.HomePost
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.example.kotlininstagramfirebasechat.utils.ValueEventListenerAdapter
import com.example.kotlininstagramfirebasechat.utils.asFeedPost
import com.example.kotlininstagramfirebasechat.utils.asUser
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    companion object {
        val TAG = HomeFragment::class.java.simpleName
    }

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var firebase: FirebaseHelper
    private lateinit var currentUid: String
    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        firebase = FirebaseHelper(context)
        currentUid = firebase.auth.currentUser!!.uid
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        home_recycler.adapter = adapter

        firebase.database.child("subscriptions/$currentUid")
            .addListenerForSingleValueEvent(ValueEventListenerAdapter { data ->
                val subscriptions = data.children.map { it.key!! }
                if (viewModel.subscriptions.value!! != subscriptions) {
                    Log.d(TAG, "update subscriptions")
                    viewModel.clearPosts()
                    viewModel.updateSubscriptions(subscriptions)
                }
                Log.d(TAG, "children: ${data.children.count()}")
            })

        viewModel.subscriptions.observe(viewLifecycleOwner, Observer {
            it.forEach { uid ->
                Log.d(TAG, "subscriptions: ${it.last()}")
                firebase.database.child("feed-posts/$uid")
                    .addListenerForSingleValueEvent(ValueEventListenerAdapter { dataPosts ->
                        dataPosts.children.forEach { dataPost ->
                            dataPost.asFeedPost()!!
                            if (!viewModel.posts.value!!.containsKey(dataPost.key!!)) {
                                Log.d(TAG, "update posts")
                                val post = dataPost.asFeedPost()!!
                                firebase.userReference(post.uid).addListenerForSingleValueEvent(ValueEventListenerAdapter{
                                    viewModel.updatePosts(post, dataPost.key!!, it.asUser())
                                })

                            }
                        }

                    })
            }
        })

        viewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            Log.d(TAG, posts.toString())
            refreshRecycler(posts.values.sortedByDescending { it.feedPost.timestampDate() })
        })
    }

    private fun refreshRecycler(posts: List<HomePost>?) {
        adapter.clear()
        posts?.forEach {
            adapter.add(HomeAdapter(it))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "destroy view")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.chats, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        findNavController().navigate(HomeFragmentDirections.actionHomeToChats())
        return true
    }
}
