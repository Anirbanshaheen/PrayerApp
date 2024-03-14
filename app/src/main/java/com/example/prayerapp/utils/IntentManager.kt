package com.example.prayerapp.utils

//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.view.View
//import com.example.prayerapp.R
//import com.example.prayerapp.utils.Constants.CHECK_DAY
//import com.example.prayerapp.utils.Constants.COLOR
//import com.example.prayerapp.utils.Constants.RESET_SALAVAT
//import com.example.prayerapp.utils.Constants.RESET_TASBIHAT
//import com.example.prayerapp.utils.Constants.RESET_ZEKR
//import com.example.prayerapp.widget.SalavatWidget
//import com.example.prayerapp.widget.TasbihatWidget
//import com.example.prayerapp.widget.ZekrWidget
//
//object IntentManager {
//
//    /**
//     * rate the app with CafeBazaar rate intent.
//     */
//    fun rateIntent(context: Context, view: View) {
//        try {
//            val intent = Intent(Intent.ACTION_EDIT)
//            intent.data = Uri.parse(context.resources.getString(R.string.bazaarStarLink))
//            intent.setPackage(context.resources.getString(R.string.bazaarPackage))
//            context.startActivity(intent)
//        } catch (e: java.lang.Exception) {
//            SnackbarManager.showSnackbar(
//                context = context,
//                view = view,
//                message = context.getString(R.string.install_bazaar_notice)
//            )
//        }
//    }
//
//    /**
//     * share a simple text and that's title with every app can share.
//     */
//    fun shareTextIntent(context: Context, view: View, title: String, description: String) {
//        try {
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.type = "text/plain"
//            intent.putExtra(Intent.EXTRA_TEXT, description)
//            context.startActivity(Intent.createChooser(intent, title))
//        } catch (e: Exception) {
//            SnackbarManager.showSnackbar(
//                context = context,
//                view = view,
//                message = context.getString(R.string.share_process_failed)
//            )
//        }
//    }
//
//    /**
//     * send a broadcast intent to salavat widget to reset the salavat counter.
//     */
//    fun resetSalavatIntent(context: Context) {
//        val intent = Intent(context, SalavatWidget::class.java)
//        intent.action = RESET_SALAVAT
//        context.sendBroadcast(intent)
//    }
//
//    /**
//     * send a broadcast intent to zekr widget to reset the zekr counter.
//     */
//    fun resetZekrIntent(context: Context) {
//        val intent = Intent(context, ZekrWidget::class.java)
//        intent.action = RESET_ZEKR
//        context.sendBroadcast(intent)
//    }
//
//    /**
//     * send a broadcast intent to tasbihat widget to reset the tasbihat all counters.
//     */
//    fun resetTasbihatIntent(context: Context) {
//        val intent = Intent(context, TasbihatWidget::class.java)
//        intent.action = RESET_TASBIHAT
//        context.sendBroadcast(intent)
//    }
//
//    /**
//     * send a broadcast intent to salavat widget to change the salavat color.
//     */
//    fun changeSalavatColorIntent(context: Context) {
//        val intent = Intent(context, SalavatWidget::class.java)
//        intent.action = COLOR
//        context.sendBroadcast(intent)
//    }
//
//    /**
//     * send a broadcast intent to zekr widget to change the zekr color.
//     */
//    fun changeZekrColorIntent(context: Context) {
//        val intent = Intent(context, ZekrWidget::class.java)
//        intent.action = COLOR
//        context.sendBroadcast(intent)
//    }
//
//    /**
//     * send a broadcast intent to tasbihat widget to change the tasbihat color.
//     */
//    fun changeTasbihatColorIntent(context: Context) {
//        val intent = Intent(context, TasbihatWidget::class.java)
//        intent.action = COLOR
//        context.sendBroadcast(intent)
//    }
//
//    /**
//     * send a broadcast intent to salavat widget to check day.
//     */
//    fun checkSalavatDayIntent(context: Context) {
//        val intent = Intent(context, SalavatWidget::class.java)
//        intent.action = CHECK_DAY
//        context.sendBroadcast(intent)
//    }
//
//    /**
//     * send a broadcast intent to zekr widget to check day.
//     */
//    fun checkZekrDayIntent(context: Context) {
//        val intent = Intent(context, ZekrWidget::class.java)
//        intent.action = CHECK_DAY
//        context.sendBroadcast(intent)
//    }
//
//}