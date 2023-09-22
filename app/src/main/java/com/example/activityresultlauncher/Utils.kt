package com.example.activityresultlauncher

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.core.app.ActivityCompat
import com.example.activityresultlauncher.model.Country
import com.example.activityresultlauncher.model.Option
import com.example.activityresultlauncher.model.PlaceModel
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TextureStyle
import okhttp3.internal.graal.TargetJdk8WithJettyBootPlatform
import java.io.File

class Utils {
    companion object {


        private const val TAG: String = "LocationService"

        private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

        private lateinit var mLastLocation: Location
        private lateinit var mLocationRequest: LocationRequest
        private lateinit var locationInterface: LocationInterface
        private val MY_PERMISSIONS_REQUEST_LOCATION = 99


        fun displayLocationSettingsRequest(context: Context, activity: Activity) {
            val googleApiClient = GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build()
            googleApiClient.connect()
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 10000
            locationRequest.fastestInterval = 10000 / 2.toLong()
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)
            val result: PendingResult<LocationSettingsResult> =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback { result ->
                val status: Status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                        Log.i("SelectLocation", "All location settings are satisfied.")
                        getLocationData(context, activity)
                    }

                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            "SelectLocation",
                            "Location settings are not satisfied. Show the user a dialog to upgrade location settings"
                        )
                        try {
                            status.startResolutionForResult(
                                activity,
                                MY_PERMISSIONS_REQUEST_LOCATION
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Log.i("SelectLocation", "PendingIntent unable to execute request.")
                        }
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                        "SelectLocation",
                        "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                    )

                    LocationSettingsStatusCodes.CANCELED -> {
                        Log.i(
                            "SelectLocation",
                            "Cancelled"
                        )
                    }
                }
            }
        }

        fun getLocationData(context: Context, activity: Activity) {
            locationInterface = (activity) as LocationInterface
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
                ) != PackageManager.PERMISSION_GRANTED && context.let {
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

        private val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation
                locationResult.lastLocation?.let { onLocationChanged(it) }
            }
        }

        fun onLocationChanged(location: Location) {

            mLastLocation = location
            Log.d(TAG, "${mLastLocation.latitude}")
            Log.d(TAG, "${mLastLocation.longitude}")

            Log.e(
                TAG,
                "onLocationChanged: " + "Location LatLng:-" + mLastLocation.latitude + " : " + mLastLocation.longitude
            )
            locationInterface.onLocationUpdate(LatLng(location.latitude, location.longitude))

            mFusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
        }

        fun list(): ArrayList<PlaceModel> {
            val list = ArrayList<PlaceModel>()
            val optionList = ArrayList<Option>()
            optionList.add(Option(title = "Test1"))
            optionList.add(Option(title = "Test2"))
            optionList.add(Option(title = "Test3"))
            val genderList = ArrayList<Option>()
            genderList.add(Option(title = "Male"))
            genderList.add(Option(title = "Female"))
            val btnList = ArrayList<Option>()
            btnList.add(Option(title = "Save"))
            btnList.add(Option(title = "Cancel"))
            val countryList = ArrayList<Country>()
            countryList.add(Country(value = "India"))
            countryList.add(Country(value = "Sri Lanka"))
            countryList.add(Country(value = "Nepal"))
            countryList.add(Country(value = "China"))
            list.add(PlaceModel(locale = "1", subLocality = "First Name", inputType = "text"))
            list.add(PlaceModel(locale = "2", subLocality = "Last Name", inputType = "text"))
            list.add(PlaceModel(locale = "3", subLocality = "City", inputType = "text"))
            list.add(PlaceModel(locale = "4", subLocality = "MobileNumber", inputType = "text"))
            list.add(
                PlaceModel(
                    locale = "5",
                    subLocality = "Gender",
                    inputType = "radio",
                    option = genderList
                )
            )
            list.add(PlaceModel(locale = "6", subLocality = "Date", inputType = "date"))
            list.add(
                PlaceModel(
                    locale = "7",
                    subLocality = "select Query Type",
                    inputType = "dropDown",
                    option = optionList
                )
            )
            list.add(
                PlaceModel(
                    locale = "8",
                    subLocality = "Country",
                    inputType = "checkBox",
                    countryList = countryList
                )
            )
            list.add(PlaceModel(locale = "9", subLocality = "Rating", inputType = "rating"))
            list.add(PlaceModel(locale = "10", inputType = "btn", option = btnList))
            return list
        }


    }
}
  /*  val type = "image/*"
    val filename = "/myPhoto.jpg"
    val mediaPath = Environment.getExternalStorageDirectory().toString() + filename
    // Create the new Intent using the 'Send' action.
    val share = Intent(Intent.ACTION_SEND)

    // Set the MIME type
    share.type = type

    // Create the URI from the media
    val media = File(mediaPath)
    val uri = Uri.fromFile(media)

    // Add the URI to the Intent.
    share.putExtra(Intent.EXTRA_STREAM, uriForCamera)

    // Broadcast the Intent.
    startActivity(Intent.createChooser(share, "Share to"))*/