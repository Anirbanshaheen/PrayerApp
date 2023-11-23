package com.example.prayerapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.prayerapp.databinding.FragmentCompassBinding
import com.example.prayerapp.ui.compass.CompassViewModel
import com.example.prayerapp.ui.compass.RotationTarget
import com.example.prayerapp.utils.exH
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CompassFragment : Fragment(), SensorEventListener {

    private lateinit var binding: FragmentCompassBinding

    private val compassViewModel by activityViewModels<CompassViewModel>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val PERMISSION_ID = 42

    private val scope = CoroutineScope(Dispatchers.Default)

    private lateinit var currentLocation: Location
    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private var currentDegree = 0f
    private var currentDegreeNeedle = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompassBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getLocationTest()
        init()
        observer()
    }


    private fun init() {
        binding.testBtn.setOnClickListener {
            //getLocationTest()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun observer() {
        compassViewModel.compassRotation.observe(viewLifecycleOwner) {
            binding.compassIV.rotation = it.to
        }

        compassViewModel.qilbaRotation.observe(viewLifecycleOwner) {
            binding.qiblaIV.rotation = it.to
        }

        compassViewModel.locationAddress.observe(viewLifecycleOwner) {
            binding.addressTV.text = it
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationTest() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

//                fusedLocationClient.lastLocation.addOnSuccessListener { location->
//                    location?.let {
//                        currentLocation = location
//                        compassViewModel.getLocationAddress(requireActivity(), currentLocation)
//                        sensorManager = requireActivity().getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
//                        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)!!
//                        sensorManager.registerListener(
//                            this, sensor, SensorManager.SENSOR_DELAY_GAME
//                        )
//                        Log.d("wow","$currentLocation")
//                    }
//                }

                fusedLocationClient.getCurrentLocation(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnCompleteListener(requireActivity()) { location ->
                    location.result?.let {
                        currentLocation = location.result
                        compassViewModel.getLocationAddress(requireActivity(), currentLocation)
                        sensorManager = requireActivity().getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
                        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)!!
                        sensorManager.registerListener(
                            this, sensor, SensorManager.SENSOR_DELAY_GAME
                        )
                        Log.d("wow","$currentLocation")
                    }
                }
            } else {
                Toast.makeText(requireActivity(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onSensorChanged(event: SensorEvent?) {
        val degree = event?.values?.get(0) ?: 0f
        val destinationLoc = Location("service Provider").apply {
            latitude = 21.422487
            longitude = 39.826206
        }

        var bearTo: Float = currentLocation.bearingTo(destinationLoc)
        if (bearTo < 0) bearTo += 360
        var direction: Float = bearTo - degree
        if (direction < 0) direction += 360

        val isFacingQibla = direction in 359.0..360.0 || direction in 0.0..1.0

        val qiblaRoation = RotationTarget(currentDegreeNeedle, direction)
        currentDegreeNeedle = direction
        val compassRotation = RotationTarget(currentDegree, -degree)
        currentDegree = -degree
        compassViewModel.updateCompass(qiblaRoation, compassRotation, isFacingQibla)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        exH { sensorManager.unregisterListener(this) }
    }
}