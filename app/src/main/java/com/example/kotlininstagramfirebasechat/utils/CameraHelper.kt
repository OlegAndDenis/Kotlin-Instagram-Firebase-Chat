package com.example.kotlininstagramfirebasechat.utils

import android.content.Intent
import androidx.fragment.app.Fragment

class CameraHelper(private val fragment: Fragment) {

    fun takeCameraPicture() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        fragment.startActivityForResult(intent, REQUEST_GALLERY_PICTURE)
    }
}