package uz.abubakir_khakimov.universal_taxi_meter.fragments

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.orhanobut.hawk.Hawk
import uz.abubakir_khakimov.universal_taxi_meter.R
import uz.abubakir_khakimov.universal_taxi_meter.databinding.FragmentHomeBinding
import uz.abubakir_khakimov.universal_taxi_meter.utils.LocationManager
import uz.abubakir_khakimov.universal_taxi_meter.utils.LocationManagerCallBack
import uz.abubakir_khakimov.universal_taxi_meter.utils.TimerManager
import uz.abubakir_khakimov.universal_taxi_meter.utils.TimerManagerCallBack
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(), OnMapReadyCallback, LocationManagerCallBack, TimerManagerCallBack {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var locationManager: LocationManager
    private lateinit var timerManager: TimerManager
    private var googleMap: GoogleMap? = null
    private var marker: Marker? = null
    private var starterState = false
    private var locationAutoMove = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = LocationManager(requireActivity())
        timerManager = TimerManager(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (this.googleMap == null) {
            this.googleMap = googleMap
            moveLastDeviceLocation()
            locationManager.runRealtimeLocation(this)
        }else{
            this.googleMap = googleMap
        }

        googleMap.setOnCameraMoveStartedListener { i ->
            if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE && locationAutoMove) {
                locationAutoMove = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMapCallBack()
        updateStarterButton()

        binding.starter.setOnClickListener {
            locationAutoMove = true

            if (starterState){
                starterButtonStopClick()
            }else{
                starterButtonStartClick()
            }
            updateStarterButton()
        }

        binding.myLocation.setOnClickListener{
            locationAutoMove = true

            if (locationManager.lastLocation != null) {
                if (starterState) {
                    changeNearCameraPosition(locationManager.lastLocation!!)
                } else {
                    changeSimpleCameraPosition(locationManager.lastLocation!!)
                }
            }
        }

        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        binding.history.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
        }

    }

    private fun updateStarterButton(){
        if (starterState){
            binding.starter.text = getString(R.string.stop)
            binding.starter.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red))
        }else{
            binding.starter.text = getString(R.string.start)
            binding.starter.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
        }
    }

    private fun starterButtonStartClick(){
        locationManager.startCalculationDistance()
        timerManager.startTimer()
        if (locationManager.lastLocation != null) {
            changeNearCameraPosition(locationManager.lastLocation!!)
        }
        marker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_tilt))

        starterState = true
    }

    private fun starterButtonStopClick(){
        locationManager.stopCalculationDistance()
        timerManager.stopTimer()
        if (locationManager.lastLocation != null) {
            changeSimpleCameraPosition(locationManager.lastLocation!!)
        }
        marker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon))

        binding.distance.text = "0 km"
        binding.timer.text = "00:00:00"
        starterState = false
    }

    override fun locationChanged(location: Location, distance: Double) {
        binding.speed.text = "${(location.speed * 3.6).toInt()} km/h"
        binding.distance.text = "${getDecimalFormat(distance)} km"
        changeMarkerPosition(location)
    }

    override fun onTick(time: Long) {
        binding.timer.text = getStringDate(time)
    }

    private fun changeNearCameraPosition(location: Location) {
        if (locationAutoMove) {
            val cameraPosition =
                CameraPosition.Builder().target(LatLng(location.latitude, location.longitude))
                    .tilt(70f)
                    .bearing(location.bearing)
                    .zoom(20f)
                    .build()
            googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    private fun changeSimpleCameraPosition(location: Location){
        if (locationAutoMove) {
            val cameraPosition =
                CameraPosition.Builder().target(LatLng(location.latitude, location.longitude))
                    .zoom(18f)
                    .build()
            googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    private fun changeMarkerPosition(location: Location){
        val latLng = LatLng(location.latitude, location.longitude)

        if (marker == null) {
            marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon))
                    .rotation(location.bearing)
                    .anchor(0.5f, 0.5f)
            )
        }else{
            marker!!.position = latLng
            marker!!.rotation = location.bearing
        }

        if (starterState){
            changeNearCameraPosition(location)
        }else {
            changeSimpleCameraPosition(location)
        }
    }

    private fun initMapCallBack(){
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun getStringDate(it: Long): String{
        return SimpleDateFormat("HH:mm:ss").format(Date(it))
    }

    private fun getDecimalFormat(it: Double): String{
        return DecimalFormat("#.##").format(it)
    }

    private fun moveLastDeviceLocation(){
        val latLng: LatLng? = Hawk.get("lastDeviceLocation", null)
        if (latLng != null) {
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        }
    }

    override fun onStop() {
        super.onStop()
        if (locationManager.lastLocation != null) {
            Hawk.put("lastDeviceLocation",
                LatLng(
                    locationManager.lastLocation!!.latitude,
                    locationManager.lastLocation!!.longitude
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.stopRealtimeLocation()
        timerManager.stopTimer()
    }

}