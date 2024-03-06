package com.example.prayerapp.prefs

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Prefs @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        const val PREF_NAME = "prayers_shared_pref"
    }

    private val instance = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var isOneTimeAlert: Boolean
        get() {
            return instance.getBoolean(::isOneTimeAlert.name, true)
        }
        set(value) {
            instance.edit().putBoolean(::isOneTimeAlert.name, value).apply()
        }

    var counterValue: Int
        get() {
            return instance.getInt(::counterValue.name, 0)
        }
        set(value) {
            instance.edit().putInt(::counterValue.name, value).apply()
        }

    var selectedValue: Int
        get() {
            return instance.getInt(::selectedValue.name, 0)
        }
        set(value) {
            instance.edit().putInt(::selectedValue.name, value).apply()
        }

    var selectedName: String?
        get() {
            return instance.getString(::selectedName.name, "")
        }
        set(value) {
            instance.edit().putString(::selectedName.name, value).apply()
        }

    var prayerBg: Int?
        get() {
            return instance.getInt(::prayerBg.name, 0)
        }
        set(value) {
            instance.edit().putInt(::prayerBg.name, value?:0).apply()
        }


}