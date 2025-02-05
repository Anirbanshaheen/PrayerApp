package com.bitbytestudio.autosilentprayerapp.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PREFERENCE_NAME = "app_preferences"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

class DataStorePreference @Inject constructor(private val context: Context) {

    fun <T> savePreference(key: Preferences.Key<T>, value: T) = CoroutineScope(Dispatchers.IO).launch {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    suspend fun clearPreferences() {
        context.dataStore.edit { it.clear() }
    }

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_DND_ENABLED = booleanPreferencesKey("is_dnd_enabled")
        val LOCATION = stringPreferencesKey("LOCATION")
    }
}