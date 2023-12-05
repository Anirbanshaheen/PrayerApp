package com.example.prayerapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.azan.Time
import java.text.SimpleDateFormat
import java.util.Locale

fun Time.twentyFourTo12HourConverter(): String {
    val hours = if(this.hour > 12) this.hour % 12 else this.hour
    val mins = if (this.minute < 10) "0" + this.minute else this.minute
    val secs = if (this.second < 10) "0" + this.second else this.second
    val amPm = if (this.hour >= 12) "PM" else "AM"

    return "$hours:$mins:$secs $amPm"
}

fun FragmentActivity.changeFragment(containerId: Int, fragment : Fragment, addToBackStack : Boolean = false) {
    val fm = supportFragmentManager.beginTransaction().replace(containerId, fragment)
    if (addToBackStack) fm.addToBackStack(null)
    fm.commit()
}

fun Context.checkLocationPermission(onMissing: () -> Unit) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        onMissing()
        return
    }
}

fun <T> exH(func: () -> T): T? {
    return try {
        func.invoke()
    } catch (e: Exception) {
        null
    }
}

fun String.toMillis(): Long {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val date = dateFormat.parse(this)
    return date?.time ?: 0L
}

fun Activity.changeStatusBarColor(@ColorInt color: Int, useLightContent: Boolean? = null) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val insetsController = window.insetsController
            val shouldUseLightContent = useLightContent ?: (ColorUtils.calculateLuminance(color) < 0.5)

            if (shouldUseLightContent) {
                insetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            }
            window.statusBarColor = color
        }else{
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = (ColorUtils.calculateLuminance(color) > 0.5)
        }
    } catch (e: Exception) {
    }
}