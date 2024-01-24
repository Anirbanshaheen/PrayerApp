package com.example.prayerapp.ui.compass

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@HiltViewModel
class CompassViewModel : ViewModel() {
    var isFacingQibla = MutableLiveData(false)
    var qilbaRotation = MutableLiveData(RotationTarget(0f, 0f))
    var compassRotation = MutableLiveData(RotationTarget(0f, 0f))

    var locationAddress = MutableLiveData("-")

    fun updateCompass(qilba: RotationTarget, compass: RotationTarget, isFacing: Boolean) {
        isFacingQibla.value = isFacing
        qilbaRotation.value = qilba
        compassRotation.value = compass
    }

    fun getLocationAddress(context: Context, location: Location) {
        viewModelScope.launch {
            Geocoder(context, Locale.getDefault()).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    getFromLocation(
                        location.latitude,
                        location.longitude,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                addresses.first()?.let {
                                    locationAddress.value = buildString {
                                        append(it.locality).append(", ")
                                        append(it.subAdminArea)
                                    }
                                }
                            }

                            override fun onError(errorMessage: String?) {
                                super.onError(errorMessage)

                            }
                        })
                } else {
                    try {
                        getFromLocation(location.latitude, location.longitude, 1)?.first()?.let {
                            locationAddress.value = buildString {
                                append(it.locality).append(", ")
                                append(it.subAdminArea)
                            }
                        }
                    }catch (e: Exception) {
                        //Log.e("PrayerError",e.message.toString())
                    }
                }
            }
        }
    }
}