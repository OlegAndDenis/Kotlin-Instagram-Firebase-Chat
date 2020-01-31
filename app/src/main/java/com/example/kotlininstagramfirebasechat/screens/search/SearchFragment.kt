package com.example.kotlininstagramfirebasechat.screens.search


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import com.example.kotlininstagramfirebasechat.MainActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.User
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.example.kotlininstagramfirebasechat.utils.hideKeyboard
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_new_user.view.*
import java.util.*

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var firebase: FirebaseHelper
    var searchList: MutableList<User> = mutableListOf()
    var adapter = GroupAdapter<ViewHolder>()

    companion object {
        private val TAG = SearchFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        firebase = FirebaseHelper()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.run {
                isIconified = false
                onActionViewExpanded()
                maxWidth = Int.MAX_VALUE

                setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean = true

                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.clear()
                        val currentList = searchList
                        if (newText != null) {
                            val search = newText.toLowerCase(Locale.getDefault())
                            currentList.forEach {
                                if (it.name.toLowerCase(Locale.getDefault()).contains(search)) {
                                    adapter.add(UserItem(it))
                                }
                            }
                        }
                        return true
                    }

                })
            }
            super.onCreateOptionsMenu(menu, inflater)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
            fetchUsers()
    }

    private fun fetchUsers() {
        val ref = firebase.database.child("users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                searchList.clear()
                adapter.clear()

                dataSnapshot.children.forEach {
                    Log.d(TAG, it.toString())
                    it.getValue(User::class.java)?.let { user ->
                        if (user.uid != firebase.auth.uid) {
                            adapter.add(UserItem(user))
                            searchList.add(user)
                        }
                    }
                }

                adapter.setOnItemClickListener { item, _ ->
                    val userItem = item as UserItem
                    val action = SearchFragmentDirections.actionSearchToProfile(userItem.user.uid)
                    findNavController().navigate(action)
                }

                recyclerview_newmessage.adapter = adapter
            }
        })
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard(activity as MainActivity)
    }

}

class UserItem(val user: User) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.name
    }

    override fun getLayout() = R.layout.item_new_user

}