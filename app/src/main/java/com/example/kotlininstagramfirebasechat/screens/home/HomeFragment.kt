package com.example.kotlininstagramfirebasechat.screens.home


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.HomePost
import com.example.kotlininstagramfirebasechat.utils.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.progress_bar.*

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

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_bar.showView()

        home_recycler.adapter = adapter

        currentUid = firebase.auth.currentUser!!.uid

        firebase.database.child("subscriptions/$currentUid")
            .addListenerForSingleValueEvent(ValueEventListenerAdapter { data ->
                val subscriptions: MutableList<String> =
                    data.children.map { it.key!! } as MutableList<String>
                subscriptions.add(currentUid)
                if (viewModel.subscriptions.value!! != subscriptions) {
                    Log.d(TAG, "update subscriptions")
                    viewModel.clearPosts()
                    viewModel.updateSubscriptions(subscriptions)
                }
                val childrenCount = data.children.count()
                Log.d(TAG, "children: $childrenCount")
                progress_bar.hideView()
            })

        viewModel.subscriptions.observe(viewLifecycleOwner, Observer {
            it.forEach { uid ->
                Log.d(TAG, "subscriptions: ${it.last()}")
                firebase.database.child("feed-posts/$uid")
                    .addValueEventListener(ValueEventListenerAdapter { dataPosts ->
                        dataPosts.children.forEach { dataPost ->
                            dataPost.asFeedPost()!!
//                            if (!viewModel.posts.value!!.containsKey(dataPost.key!!)) {
                                Log.d(TAG, "update posts")
                                val post = dataPost.asFeedPost()!!
                                firebase.userReference(post.uid)
                                    .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                                        viewModel.updatePosts(post, dataPost.key!!, it.asUser())
                                    })

//                            }
                        }
                    })
            }
        })

        viewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            Log.d(TAG, posts.toString())
            home_placeholder.visibility = if (posts.size == 0) View.VISIBLE else View.GONE
            refreshRecycler(posts.values.sortedByDescending { it.feedPost.timestampDate() })
        })
    }

    private fun refreshRecycler(posts: List<HomePost>?) {
        adapter.clear()
        posts?.forEach {
            adapter.add(HomeAdapter(it) { post ->
                val ref = firebase.database
                    .child("feed-posts/${post.user.uid}/${post.key}/likes/$currentUid")
                if (post.feedPost.likes.containsKey(currentUid)) {
                    ref.removeValue()
                } else ref.setValue(java.lang.Boolean.TRUE)
            })
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
