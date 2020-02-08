package com.example.kotlininstagramfirebasechat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper
import com.example.kotlininstagramfirebasechat.utils.ValueEventListenerAdapter
import com.example.kotlininstagramfirebasechat.utils.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    private lateinit var db: DatabaseReference
    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "create")

        db = FirebaseDatabase.getInstance().reference

        FirebaseHelper(this).auth.addAuthStateListener {
            if (it.currentUser == null) {
                Log.d(TAG, "current user = null")
                finish()
                startActivity(Intent(this, StartActivity::class.java))
            } else {
                manageConnections()

            }
        }

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState!!)
        setupBottomNavigationBar()
    }

    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navGraphIds = listOf(R.navigation.home, R.navigation.share, R.navigation.search, R.navigation.profile)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment_main,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.chatFragment -> hideBottomNav()
                    else -> showBottomNav()
                }
            }
        })
        currentNavController = controller
    }

    private fun showBottomNav() {
        bottom_nav.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        bottom_nav.visibility = View.GONE
    }


    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun manageConnections() {
        val connectionReference = db.child("connections")
        val lastConnected = db.child("last-connected/${FirebaseAuth.getInstance().currentUser!!.uid}")
        val infoConnected = db.child(".info/connected")

        infoConnected.addValueEventListener(ValueEventListenerAdapter { data ->
            data.getValue(Boolean::class.java).let {
                if (it!!) {
                    val con = connectionReference.child(FirebaseAuth.getInstance().currentUser!!.uid)
                    con.setValue(java.lang.Boolean.TRUE)
                    con.onDisconnect().removeValue()
                    lastConnected.onDisconnect().setValue(ServerValue.TIMESTAMP)
                }
            }


        })
    }

}
