package com.bitbytestudio.autosilentprayerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azan.AzanTimes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor() : ViewModel() {

    val prayersTime = MutableSharedFlow<AzanTimes>()

    private var _fragmentSwitchState = MutableSharedFlow<Int>()
    val fragmentSwitchState = _fragmentSwitchState

    fun setFragmentSwitchState(fragmentState: Int) = viewModelScope.launch {
        _fragmentSwitchState.emit(fragmentState)
    }

}