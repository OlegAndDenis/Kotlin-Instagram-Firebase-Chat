package com.example.kotlininstagramfirebasechat.screens.search


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_new_user.view.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment(R.layout.fragment_search) {

    companion object {
        const val USER_KEY = "USER_KEY"
        private val TAG = SearchFragment::class.java.simpleName
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchUsers()
    }

    private fun fetchUsers() {

        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                dataSnapshot.children.forEach {
                    Log.d(TAG, it.toString())
                    @Suppress("NestedLambdaShadowedImplicitParameter")
                    it.getValue(User::class.java)?.let {
                        if (it.uid != FirebaseAuth.getInstance().uid) {
                            adapter.add(UserItem(it, context!!))
                        }
                    }
                }

//                adapter.setOnItemClickListener { item, view ->
//                    val userItem = item as UserItem
//                    val intent = Intent(view.context, ChatLogActivity::class.java)
//                    intent.putExtra(USER_KEY, userItem.user)
//                    startActivity(intent)
//                    finish()
//                }

                recyclerview_newmessage.adapter = adapter
            }

        })
    }
}

class UserItem(private val user: User, private val context: Context) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.name
    }

    override fun getLayout(): Int {
        return R.layout.item_new_user
    }

}