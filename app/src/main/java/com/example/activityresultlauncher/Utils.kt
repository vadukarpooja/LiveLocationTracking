package com.example.activityresultlauncher

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import java.sql.Driver
import kotlin.contracts.contract
import kotlin.coroutines.coroutineContext

class Utils {
    companion object {


        private val TAG: String = "LocationService"

        private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

        lateinit var mLastLocation: Location
        lateinit var mLocationRequest: LocationRequest
        lateinit var locationInterface: LocationInterface




         fun getLocationData(context: Context,activity: Activity) {
            locationInterface = (activity)as LocationInterface
            startLocationUpdates(context)
        }

        private fun startLocationUpdates(context: Context) {
         mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .apply {
                    setWaitForAccurateLocation(false)
                    setMinUpdateIntervalMillis(IMPLICIT_MIN_UPDATE_INTERVAL)
                    setMaxUpdateDelayMillis(100000)
                }.build()

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest)
            val locationSettingsRequest = builder.build()

            val settingsClient =
                LocationServices.getSettingsClient(context.applicationContext)
            settingsClient.checkLocationSettings(locationSettingsRequest)

            mFusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context.applicationContext)


            if (ActivityCompat.checkSelfPermission(
                    context.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            Looper.myLooper()?.let {
                mFusedLocationProviderClient!!.requestLocationUpdates(
                    mLocationRequest, mLocationCallback,
                    it
                )
            }
        }

         val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation
                locationResult.lastLocation?.let { onLocationChanged(it,) }
            }
        }

        fun onLocationChanged(location: Location) {

            mLastLocation = location
            Log.d(TAG, "${mLastLocation.latitude}")
            Log.d(TAG, "${mLastLocation.longitude}")

            Log.e(TAG, "onLocationChanged: "+ "Location LatLng:-" + mLastLocation.latitude + " : " + mLastLocation.longitude, )
            locationInterface.onLocationChange(mLastLocation.latitude, mLastLocation.longitude)

            mFusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
        }

    }

}