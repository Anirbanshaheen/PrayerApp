package com.example.prayerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayerapp.model.PrayersTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor() : ViewModel() {

    private var _prayersTime = MutableSharedFlow<ArrayList<PrayersTime>>()
    val prayersTime = _prayersTime

    private var _fragmentSwitchState = MutableSharedFlow<Int>()
    val fragmentSwitchState = _fragmentSwitchState

    fun setPrayerTimes(prayersTime: ArrayList<PrayersTime>) = viewModelScope.launch {
        _prayersTime.emit(prayersTime)
    }

    fun setFragmentSwitchState(fragmentState: Int) = viewModelScope.launch {
        _fragmentSwitchState.emit(fragmentState)
    }

}