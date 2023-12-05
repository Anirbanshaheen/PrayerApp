package com.example.prayerapp.ui

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Define a callback interface
interface SystemTimeCallback {
    fun onSystemTimeReceived(systemTime: String)
}

// Class that provides continuous updates of the system time using coroutines
class TimeTickerProvider {

    private var callback: SystemTimeCallback? = null
    private var isRunning = false

    // Set the callback
    fun setCallback(callback: SystemTimeCallback) {
        this.callback = callback
    }

    // Start continuous updates of system time
    fun startContinuousUpdates() {
        if (!isRunning) {
            isRunning = true
            // Start a coroutine for continuous updates
            CoroutineScope(Dispatchers.Main).launch {
                while (isRunning) {
                    callback?.onSystemTimeReceived(getFormattedSystemTime())
                    delay(UPDATE_INTERVAL_MILLIS)
                }
            }
        }
    }

    // Stop continuous updates
    fun stopContinuousUpdates() {
        isRunning = false
    }

    // Get the current system time in the desired format
    private fun getFormattedSystemTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    companion object {
        private const val UPDATE_INTERVAL_MILLIS = 1000L // 1 second interval
    }
}