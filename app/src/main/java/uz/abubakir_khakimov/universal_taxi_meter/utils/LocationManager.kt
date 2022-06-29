package uz.abubakir_khakimov.universal_taxi_meter.utils

import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface LocationManagerCallBack{
    fun locationChanged(location: Location, distance: Double)
}

class LocationManager(private val context: Context) {

    private var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationManagerCallBack: LocationManagerCallBack? = null
    private var starterState = false

    var lastLocation: Location? = null // speed = km/h
    var lastDistance = 0.0 // km

    private val locationCallBack = object: LocationCallback(){
        override fun onLocationResult(locResult: LocationResult) {
            super.onLocationResult(locResult)

            if (lastLocation != null && starterState){
                lastDistance += lastLocation!!.distanceTo(locResult.lastLocation) / 1000.0
            }

            lastLocation = locResult.lastLocation
            locationManagerCallBack?.locationChanged(lastLocation!!, lastDistance)
        }
    }

    fun startCalculationDistance(){
        starterState = true
    }

    fun stopCalculationDistance(){
        starterState = false
        lastDistance = 0.0
    }

    fun runRealtimeLocation(locationManagerCallBack: LocationManagerCallBack) {
        this.locationManagerCallBack = locationManagerCallBack

        val locationRequest = LocationRequest.create()
        locationRequest.interval = 500
        locationRequest.fastestInterval = 500
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            Looper.getMainLooper()
        )
    }

    fun stopRealtimeLocation(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
        locationManagerCallBack = null
    }

    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Long {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lng2 - lng1)
        val a = (sin(dLat / 2) * sin(dLat / 2)
                + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2)))
        val c = 2 * asin(sqrt(a))
        return Math.round(6371000 * c)
    }

}