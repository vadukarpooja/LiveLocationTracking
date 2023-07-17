package com.example.activityresultlauncher

import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
/*
import kotlin.coroutines.jvm.internal.CompletedContinuation.context
*/

class LocationUpdatesComponent(private var iLocationProvider: ILocationProvider?) {
    private lateinit var mLocationRequest: LocationRequest

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    /**
     * Callback for changes in location.
     */
    private var mLocationCallback: LocationCallback? = null

    /**
     * The current location.
     */
    private var mLocation: Location? = null

    /**
     * create first time to initialize the location components
     *
     * @param context
     */
    fun onCreate(context: Context?) {
        Log.e(TAG, "created...............")
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.e(
                    TAG,
                    "onCreate...onLocationResult...............loc " + locationResult.lastLocation
                )
                onNewLocation(locationResult.lastLocation)
            }
        }
        // create location request
        createLocationRequest(context)
        // get last known location
        lastLocation
    }

    /**
     * start location updates
     */
    fun onStart() {
        Log.i(TAG, "onStart ")
        //hey request for location updates
        requestLocationUpdates()
    }

    /**
     * remove location updates
     */
    fun onStop() {
        Log.e(TAG, "onStop....")
        removeLocationUpdates()
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
     fun requestLocationUpdates() {
        Log.e(TAG, "Requesting location updates")
        try {
            mLocationRequest.let {
                mLocationCallback?.let { it1 ->
                    mFusedLocationClient!!.requestLocationUpdates(
                        it,
                        it1, Looper.getMainLooper()
                    )
                }
            }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * [SecurityException].
     */
    private fun removeLocationUpdates() {
        Log.e(TAG, "Removing location updates")
        try {
            mLocationCallback?.let { mFusedLocationClient!!.removeLocationUpdates(it) }
           /** Utils.setRequestingLocationUpdates(this, false);
            stopSelf()*/
        } catch (unlikely: SecurityException) {
          /**  Utils.setRequestingLocationUpdates(this, true);*/
            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }
    }   /*Toast.makeText(getApplicationContext(), "" + mLocation, Toast.LENGTH_SHORT).show();*/

    /**
     * get last location
     */
    private val lastLocation: Unit
        get() {
            try {
                mFusedLocationClient!!.lastLocation
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            mLocation = task.result
                            Log.i(TAG, "getLastLocation $mLocation")
                            /*Toast.makeText(getApplicationContext(), "" + mLocation, Toast.LENGTH_SHORT).show();*/
                            onNewLocation(mLocation)
                        } else {
                            Log.w(TAG, "Failed to get location.")
                        }
                    }
            } catch (unlikely: SecurityException) {
                Log.e(TAG, "Lost location permission.$unlikely")
            }
        }

    private fun onNewLocation(location: Location?) {
        Log.e(TAG, "New location: $location")
        /*Toast.makeText(getApplicationContext(), "onNewLocation " + location, Toast.LENGTH_LONG).show();*/
        mLocation = location
        iLocationProvider!!.onLocationUpdate(mLocation)
    }

    /**
     * Sets the location request parameters.
     */
    private fun createLocationRequest(context: Context) {
       mLocationRequest =  LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
            .apply {
                setWaitForAccurateLocation(true)
                setMinUpdateIntervalMillis(LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL)
                setMaxUpdateDelayMillis(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
            }.build()

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient =
            LocationServices.getSettingsClient(context.applicationContext)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context.applicationContext)

        /*mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY*/
    }

    /**
     * implements this interface to get call back of location changes
     */
    interface ILocationProvider {
        fun onLocationUpdate(location: Location?)
    }

    companion object {
        private val TAG = LocationUpdatesComponent::class.java.simpleName

        /**
         * The desired interval for location updates. Inexact. Updates may be more or less frequent.
         */
        private const val UPDATE_INTERVAL_IN_MILLISECONDS = (3 * 1000).toLong()

        /**
         * The fastest rate for active location updates. Updates will never be more frequent
         * than this value.
         */
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }
}