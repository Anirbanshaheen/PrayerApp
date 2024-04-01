package com.bitbytestudio.prayerapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PrayersApp: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory : HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        //Hawk.init(this.applicationContext).build()
        //registerReceiver(TimeChangedReceiver(), IntentFilter(Intent.ACTION_TIME_TICK))
    }
}