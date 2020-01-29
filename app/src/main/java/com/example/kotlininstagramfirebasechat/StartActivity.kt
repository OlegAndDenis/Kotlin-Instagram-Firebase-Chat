package com.example.kotlininstagramfirebasechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.kotlininstagramfirebasechat.utils.FirebaseHelper

class StartActivity : AppCompatActivity() {

    companion object {
        val TAG = StartActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        Log.d(TAG, "created")
        FirebaseHelper().auth.addAuthStateListener {
            if (it.currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        val navController = this.findNavController(R.id.nav_host_fragment_start)
        NavigationUI.setupActionBarWithNavController(this,navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment_start)
        return navController.navigateUp()
    }
}
