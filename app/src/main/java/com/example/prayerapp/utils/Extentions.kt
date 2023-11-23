package com.example.prayerapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.azan.Time

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