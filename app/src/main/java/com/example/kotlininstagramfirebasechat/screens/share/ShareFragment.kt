package com.example.kotlininstagramfirebasechat.screens.share


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment

import com.example.kotlininstagramfirebasechat.R
import com.example.kotlininstagramfirebasechat.models.FeedPost
import com.example.kotlininstagramfirebasechat.utils.*
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_share.*
import kotlinx.android.synthetic.main.progress_bar.*
import java.lang.Exception
import java.util.*

class ShareFragment : Fragment(R.layout.fragment_share) {

    companion object {
        val TAG = ShareFragment::class.java.simpleName
    }

    private var imageUri: Uri? = null
    private lateinit var camera: CameraHelper
    private lateinit var firebase: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        camera = CameraHelper(this)
        firebase = FirebaseHelper(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        camera.takeCameraPicture()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.share, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share) share()
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_GALLERY_PICTURE && resultCode == RESULT_OK) {
            try {
                imageUri = data!!.data
                GlideApp.with(context!!).load(imageUri).centerCrop().into(share_image)
            } catch (e: Exception) {
                Log.d(TAG, e.message ?: return)
            }
        }
    }

    private fun share() {
        val uri = if (imageUri != null) imageUri else null
        if (uri != null) {
            progress_bar.showView()
            val uid = firebase.auth.currentUser!!.uid
            updateStorage(uid, uri)
        }
    }

    private fun updateStorage(uid: String, uri: Uri) {
        firebase.storage.child("users").child(uid).child("images")
            .child(uri.lastPathSegment ?: return).putFile(uri).addOnCompleteListener {
                if (it.isSuccessful) {
                    downloadImage(uri, uid)
                } else {
                    showToast(context, it.exception?.message)
                    progress_bar.hideView()
                }
            }
    }

    private fun downloadImage(uri: Uri, uid: String) {
        firebase.storageShare(uri).addOnCompleteListener {
            val imageUrl = it.result.toString()
            updateDatabase(uid, imageUrl)
        }
    }

    private fun updateDatabase(uid: String, imageUrl: String) {
        firebase.database.child("feed-posts").child(uid)
            .push()
            .setValue(mkFeedPost(uid, imageUrl))
            .addOnCompleteListener {
                progress_bar.hideView()
                if (it.isSuccessful) clearViews() else showToast(context, it.exception?.message)
            }
    }

    private fun clearViews() {
        imageUri = null
        share_image.setImageResource(R.drawable.portrait_placeholder)
        share_capture_input.setText("")
    }

    private fun mkFeedPost(uid: String, imageUrl: String) =
        FeedPost(
            uid = uid,
            image = imageUrl,
            caption = share_capture_input.text.toString()
        )

    override fun onDestroyView() {
        super.onDestroyView()
        clearViews()
    }
}



