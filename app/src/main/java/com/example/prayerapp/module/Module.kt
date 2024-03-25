package com.example.prayerapp.module

import android.content.Context
import com.example.prayerapp.prefs.Prefs
import com.example.prayerapp.receiver.PrayersAlertReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class Module {
    @Provides
    @Singleton
    fun providerPrefs(@ApplicationContext context: Context) = Prefs(context)
}