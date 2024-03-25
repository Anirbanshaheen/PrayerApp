package com.example.prayerapp.prefs

import android.content.Context
import androidx.annotation.ColorInt
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject

class Prefs @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        const val PREF_NAME = "prayers_shared_pref"
    }
    val gson = Gson()

    val instance = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var appLanguage: String
        get() {
            return instance.getString(::appLanguage.name, "en")?:"en"
        }
        set(value) {
            instance.edit().putString(::appLanguage.name, value).apply()
        }

    var statusBarColor: Int
        get() {
            return instance.getInt(::statusBarColor.name, 0)
        }
        set(value) {
            instance.edit().putInt(::statusBarColor.name, value).apply()
        }

    var workerId: UUID?
        get() {
            return try {
                UUID.fromString(instance.getString(::workerId.name, "") ?: "")
            }catch (_: Exception){
                null
            }
        }
        set(value) {
            instance.edit().putString(::workerId.name, value.toString()).apply()
        }

    var isOneTimeAlert: Boolean
        get() {
            return instance.getBoolean(::isOneTimeAlert.name, true)
        }
        set(value) {
            instance.edit().putBoolean(::isOneTimeAlert.name, value).apply()
        }

    var currentLat: Double
        get() {
            return (instance.getFloat(::currentLat.name, 0f)).toDouble()
        }
        set(value) {
            instance.edit().putFloat(::currentLat.name, value.toFloat()).apply()
        }

    var currentLon: Double
        get() {
            return (instance.getFloat(::currentLon.name, 0f)).toDouble()
        }
        set(value) {
            instance.edit().putFloat(::currentLon.name, value.toFloat()).apply()
        }


    var selectedValue: String
        get() {
            return instance.getString(::selectedValue.name, "Subhanallah")?:"Subhanallah"
        }
        set(value) {
            instance.edit().putString(::selectedValue.name, value).apply()
        }

    fun <T> save(gModel: T, key: String = selectedValue) {
        val jsonString = gson.toJson(gModel)
        instance.edit().putString(key, jsonString).apply()
    }
    inline fun <reified T> get(key: String = selectedValue): T? {
        val value = instance.getString(key, null)
        return gson.fromJson(value, T::class.java)
    }

    data class TasbihModel(
        var totalCount: Int = 0,
        var maxCount: Int = 33,
        var name: String = "Subhanallah",
        var selectedBtnText: String = "Subhanallah"
    )
}