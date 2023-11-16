package com.example.prayerapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class PrayersApp: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}