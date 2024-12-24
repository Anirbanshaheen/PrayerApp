package com.bitbytestudio.autosilentprayerapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.bitbytestudio.autosilentprayerapp.databinding.ViewSnackbarBinding
import com.google.android.material.snackbar.Snackbar

object SnackbarManager {

    /**
     * show snackbar message in current given view.
     */
    @SuppressLint("RestrictedApi")
    fun showSnackbar(context: Context, view: View, message: String) {
        val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
        val customSnackView = ViewSnackbarBinding.inflate(LayoutInflater.from(context)).apply {
            tvMessage.text = message
        }
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)
        (snackbar.view as Snackbar.SnackbarLayout).apply {
            setPadding(0, 0, 0, 120)
            addView(customSnackView.root, 0)
        }
        snackbar.show()
    }

}