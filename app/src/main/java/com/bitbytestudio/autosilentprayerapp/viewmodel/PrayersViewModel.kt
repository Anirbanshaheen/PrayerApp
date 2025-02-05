package com.bitbytestudio.autosilentprayerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azan.Azan
import com.azan.AzanTimes
import com.azan.Method
import com.azan.astrologicalCalc.SimpleDate
import com.bitbytestudio.autosilentprayerapp.prefs.Prefs
import com.bitbytestudio.autosilentprayerapp.utils.twentyFourTo12HourConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.GregorianCalendar
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(private val prefs: Prefs) : ViewModel() {

    private var _prayersTime = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val prayersTime = _prayersTime.asStateFlow()
    private val _currentPrayerSession = MutableStateFlow<Pair<String, String>?>(null)
    val currentPrayerSession = _currentPrayerSession.asStateFlow()

    private var _fragmentSwitchState = MutableSharedFlow<Int>()
    val fragmentSwitchState = _fragmentSwitchState

    fun setFragmentSwitchState(fragmentState: Int) = viewModelScope.launch {
        _fragmentSwitchState.emit(fragmentState)
    }

    fun getPrayersTime() {
        val today = SimpleDate(GregorianCalendar())
        val location = com.azan.astrologicalCalc.Location(prefs.currentLat, prefs.currentLon, +6.0, 0)
        val azan = Azan(location, Method.KARACHI_HANAF)
        val prayerTimes = azan.getPrayerTimes(today)

        // Convert prayer times to milliseconds
        val prayerTimeInMillis = mapOf(
            "Fajr" to timeToMilliSecond(prayerTimes.fajr().hour, prayerTimes.fajr().minute),
            "Dhuhr" to timeToMilliSecond(prayerTimes.thuhr().hour, prayerTimes.thuhr().minute),
            "Asr" to timeToMilliSecond(prayerTimes.assr().hour, prayerTimes.assr().minute),
            "Maghrib" to timeToMilliSecond(prayerTimes.maghrib().hour, prayerTimes.maghrib().minute),
            "Isha" to timeToMilliSecond(prayerTimes.ishaa().hour, prayerTimes.ishaa().minute)
        )

        // Convert prayer times to 12-hour format
        val prayerTimeStrings = mapOf(
            "Fajr" to prayerTimes.fajr().twentyFourTo12HourConverter(),
            "Dhuhr" to prayerTimes.thuhr().twentyFourTo12HourConverter(),
            "Asr" to prayerTimes.assr().twentyFourTo12HourConverter(),
            "Maghrib" to prayerTimes.maghrib().twentyFourTo12HourConverter(),
            "Isha" to prayerTimes.ishaa().twentyFourTo12HourConverter()
        )

        // Get current time in milliseconds
        val currentTimeMillis = System.currentTimeMillis()

        // Check which prayer time is closest to the current time
        when {
            currentTimeMillis in ((prayerTimeInMillis["Isha"]?:0) + (10 * 3600000))..((prayerTimeInMillis["Fajr"]?:0) + (30 * 60000)) -> _currentPrayerSession.value = Pair("Fajr", prayerTimeStrings["Fajr"]?:"")
            currentTimeMillis in ((prayerTimeInMillis["Fajr"]?:0) + (30 * 60000))..((prayerTimeInMillis["Dhuhr"]?:0) + (3 * 3600000)) -> _currentPrayerSession.value = Pair("Dhuhr", prayerTimeStrings["Dhuhr"]?:"")
            currentTimeMillis in ((prayerTimeInMillis["Dhuhr"]?:0) + (3 * 3600000))..((prayerTimeInMillis["Asr"]?:0) + 3600000) -> _currentPrayerSession.value = Pair("Asr", prayerTimeStrings["Asr"]?:"")
            currentTimeMillis in ((prayerTimeInMillis["Asr"]?:0) + 3600000)..((prayerTimeInMillis["Maghrib"]?:0) + (20 * 60000)) -> _currentPrayerSession.value = Pair("Maghrib", prayerTimeStrings["Maghrib"]?:"")
            currentTimeMillis in ((prayerTimeInMillis["Maghrib"]?:0) + (20 * 60000))..((prayerTimeInMillis["Isha"]?:0) + (5 * 3600000)) -> _currentPrayerSession.value = Pair("Isha", prayerTimeStrings["Isha"]?:"")
        }

        _prayersTime.value = listOf(
            Pair("Fajr", prayerTimeStrings["Fajr"]?:""),
            Pair("Dhuhr",prayerTimeStrings["Dhuhr"]?:""),
            Pair("Asr",prayerTimeStrings["Asr"]?:""),
            Pair("Maghrib",prayerTimeStrings["Maghrib"]?:""),
            Pair("Isha",prayerTimeStrings["Isha"]?:"")
        )
    }

    private fun timeToMilliSecond(hours: Int, minutes: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}