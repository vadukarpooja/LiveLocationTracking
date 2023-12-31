package com.example.activityresultlauncher

import android.annotation.TargetApi
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.example.activityresultlauncher.databinding.ActivityGeofenceBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

class GeofenceActivity : AppCompatActivity() {

    /*
        private lateinit var appBarConfiguration: AppBarConfiguration
    */
    private val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 3 // random unique value
    private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 4
    private val REQUEST_TURN_DEVICE_LOCATION_ON = 5
    private lateinit var binding: ActivityGeofenceBinding
    private val gadgetQ = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityGeofenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        /*val navController = findNavController(R.id.nav_host_fragment_content_geofence)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)*/

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.fab)
                .setAction("Action", null).show()
        }
    }

    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_geofence)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE ||
            requestCode == REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                validateGadgetAreaInitiateGeofence()
            }
        }
    }
    @TargetApi(29)
    private fun approveForegroundAndBackgroundLocation(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION
                ))
        val backgroundPermissionApproved =
            if (gadgetQ) {
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun authorizedLocation(): Boolean {
        val formalizeForeground = (
                PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION
                ))
        val formalizeBackground =
            if (gadgetQ) {
                PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else {
                true
            }
        return formalizeForeground && formalizeBackground
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun askLocationPermission() {
        if (authorizedLocation())
            return
        var grantingPermission = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        val customResult = when {
            gadgetQ -> {
                grantingPermission += android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }
        Log.e(javaClass.simpleName, "askLocationPermission")

        ActivityCompat.requestPermissions(
            this, grantingPermission, customResult
        )
    }

    private fun validateGadgetAreaInitiateGeofence(resolve: Boolean = true) {

        // create a location request that request for the quality of service to update the location
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .apply {
                setWaitForAccurateLocation(false)
                setMinUpdateIntervalMillis(LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL)
                setMaxUpdateDelayMillis(100000)
            }.build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        // check if the client location settings are satisfied
        val client = LocationServices.getSettingsClient(this)

        // create a location response that acts as a listener for the device location if enabled
        val locationResponses = client.checkLocationSettings(builder.build())

        locationResponses.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    exception.startResolutionForResult(
                        this, REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(javaClass.simpleName, "Error getting location settings resolution: ${sendEx.message}")
                }
            } else {
                Toast.makeText(this, "Enable your location", Toast.LENGTH_SHORT).show()
            }
        }

        locationResponses.addOnCompleteListener {it ->
            if (it.isSuccessful) {
                /*addGeofence()*/
            }
        }
    }


}