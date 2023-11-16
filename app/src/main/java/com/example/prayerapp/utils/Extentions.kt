package com.example.prayerapp.utils

import com.azan.Time

fun Time.twentyFourTo12HourConverter(): String {
    val hours = if(this.hour > 12) this.hour % 12 else this.hour
    val mins = if (this.minute < 10) "0" + this.minute else this.minute
    val secs = if (this.second < 10) "0" + this.second else this.second
    val amPm = if (this.hour >= 12) "PM" else "AM"

    return "$hours:$mins:$secs $amPm"
}