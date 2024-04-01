package com.example.prayerapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
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
import com.example.prayerapp.prefs.Prefs
import com.example.prayerapp.ui.compass.CompassViewModel
import com.example.prayerapp.ui.compass.RotationTarget
import com.example.prayerapp.utils.exH
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CompassFragment : Fragment(), SensorEventListener {

    private lateinit var binding: FragmentCompassBinding
    private lateinit var a: MainActivity

    private val compassViewModel by activityViewModels<CompassViewModel>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val PERMISSION_ID = 42

    private lateinit var currentLoctionSensor: Location
    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private var currentDegree = 0f
    private var currentDegreeNeedle = 0f

    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //prefs = Prefs(requireActivity())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        a = (requireActivity() as MainActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompassBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.compassTB.backgroundTintList = ColorStateList.valueOf(prefs.statusBarColor)

        init()
        observer()
    }


    @SuppressLint("MissingPermission")
    private fun init() {
        currentLoctionSensor = Location("service Provider").apply {
            latitude = prefs.currentLat
            longitude = prefs.currentLon
        }

        a.myLocationManager?.locationCallback = object : (Location?) -> Unit {
            override fun invoke(location: Location?) {
                location?.let {
                    prefs.currentLat = it.latitude
                    prefs.currentLon = it.longitude
                    currentLoctionSensor = it.apply {
                        latitude = it.latitude
                        longitude = it.longitude
                    }
                    kotlin.runCatching {
                        compassViewModel.getLocationAddress(
                            requireContext(),
                            prefs.currentLat,
                            prefs.currentLon
                        )
                    }
                }
            }
        }
        a.myLocationManager?.initialize()

        sensorManager = requireContext().getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) ?: sensor
        if (sensor == null) {
            Toast.makeText(requireContext(), "No sensor available!", Toast.LENGTH_LONG).show()
            return
        }
        sensorManager?.registerListener(
            this, sensor, SensorManager.SENSOR_DELAY_UI
        )
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

//    private fun enableLocation() {
//        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).apply {
//            setMinUpdateDistanceMeters(1f)
//            setWaitForAccurateLocation(true)
//        }.build()
//
//        val builder = LocationSettingsRequest.Builder()
//            .addLocationRequest(locationRequest)
//
//        LocationServices.getSettingsClient(requireActivity())
//            .checkLocationSettings(builder.build())
//            .addOnSuccessListener { response ->
//
//                // Location settings are satisfied, start updating location
//                // startUpdatingLocation(...)
//            }
//            .addOnFailureListener { ex ->
//                if (ex is ResolvableApiException) {
//                    // Location settings are NOT satisfied, but this can be fixed by showing the user a dialog.
//                    try {
//                        // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
//                        val resolvable = ex as ResolvableApiException
//                        resolvable.startResolutionForResult(requireActivity(), 11)
//                    } catch (sendEx: IntentSender.SendIntentException) {
//                        // Ignore the error.
//                    }
//                }
//            }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun getLocationTest() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//
//                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnCompleteListener(requireActivity()) { location ->
//                    location.result?.let {
//                        currentLocation = location.result
//                        kotlin.runCatching { compassViewModel.getLocationAddress(requireContext(), currentLocation) }
//                        kotlin.runCatching { sensorManager = requireContext().getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager }
//                        if (sensorManager == null) {
//                            Toast.makeText(requireContext(), "No sensor available!", Toast.LENGTH_LONG).show()
//                            return@addOnCompleteListener
//                        }
//                        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION) ?: sensor
//                        if (sensor == null) return@addOnCompleteListener
//                        sensorManager?.registerListener(
//                            this, sensor, SensorManager.SENSOR_DELAY_GAME
//                        )
//                        Log.d("wow","$currentLocation")
//                    }
//                }
//            } else {
//                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
//            }
//        } else {
//            requestPermissions()
//        }
//    }

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
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }


    override fun onSensorChanged(event: SensorEvent?) {
        val degree = event?.values?.get(0) ?: 0f
        val qiblaLocation = Location("service Provider").apply {
            latitude = 21.422487 // qibla lat lon
            longitude = 39.826206
        }

        var bearTo: Float = currentLoctionSensor.bearingTo(qiblaLocation)
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
        exH { sensorManager?.unregisterListener(this) }
    }

    companion object {
        fun newInstance() = CompassFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}