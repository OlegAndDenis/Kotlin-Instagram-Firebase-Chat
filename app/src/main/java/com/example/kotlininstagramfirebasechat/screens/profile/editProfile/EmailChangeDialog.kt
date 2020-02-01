package com.example.kotlininstagramfirebasechat.screens.profile.editProfile

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.kotlininstagramfirebasechat.R
import kotlinx.android.synthetic.main.dialog_change_email.view.*

class EmailChangeDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel: EditProfileViewModel by activityViewModels()
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_change_email, null)
        return AlertDialog.Builder(context!!)
            .setView(view)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.setPassword(view.dialog_password_input.text.toString())
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                // do nothing
            }
            .setTitle(R.string.please_enter_password)
            .create()
    }
}