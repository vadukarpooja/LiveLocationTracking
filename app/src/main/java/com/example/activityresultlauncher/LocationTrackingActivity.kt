package com.example.activityresultlauncher

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.activityresultlauncher.Utils.Companion.toNullString
import com.example.activityresultlauncher.databinding.ActivityLocationTrackingBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.text.DateFormat
import java.util.Date

class LocationTrackingActivity : AppCompatActivity(), OnMapReadyCallback,LocationInterface {
    lateinit var binding: ActivityLocationTrackingBinding

    /** as google doc says*/
    /** Handler for incoming messages from the service.*/
    private lateinit var mMap: GoogleMap
    private var mCurrLocationMarker: Marker? = null
    private var latitudeCurrent = 0.0
    private var longitudeCurrent = 0.0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    ) && (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
                    == PackageManager.PERMISSION_GRANTED)
        ) {
            Utils.getLocationData(this,this)
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                @SuppressLint("SetTextI18n")
                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                override fun onQueryTextSubmit(query: String?): Boolean {
                    binding.group.visibility = View.VISIBLE
                    /** location name from search view.*/
                    val location: String = binding.searchView.query.toString()

                    /** where we will store the list of all address.*/
                    var addressList: MutableList<Address?>? = null

                    /** on below line we are creating and initializing a geo coder. */


                    val geocoder = Geocoder(applicationContext)
                    try {
                        /** location name and adding that location to address list.*/
                        addressList = geocoder.getFromLocationName(location, 1);
                        Log.e(javaClass.simpleName, "addresses1: $addressList")
                        /** For Android SDK < 33, the addresses list will be still obtained from the getFromLocation() method*/
                        if (!addressList.isNullOrEmpty()) {
                            val address: Address? = addressList[0]
                            if (address != null) {
                                Log.e(javaClass.simpleName, "address: $address")
                                binding.locale.text = "Place : " + address.getAddressLine(0).toNullString()
                                binding.subLocality.text =
                                    "SubLocality : " + address.subLocality.toNullString()
                                binding.postalCode.text =
                                    "PostalCode : " + address.postalCode.toNullString()
                                /** where we will add our locations latitude and longitude.*/
                                val latLng = LatLng(address.latitude, address.longitude)
                                mMap.addMarker(MarkerOptions().position(latLng))
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                            }
                        }


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    /** on below line we are adding marker to that position.ap.addMarker(MarkerOptions().position(latLng).title(location))*/
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })

        } else {
            checkLocationPermission()
        }
    }
    @SuppressLint("SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.addMarker(MarkerOptions().position(LatLng(latitudeCurrent,longitudeCurrent)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitudeCurrent,longitudeCurrent), 15f))
        /**
        Setting a click event handler for the map
         */
        val markerOptions = MarkerOptions()
        mMap.setOnMapClickListener { latLng -> // Creating a marker

            /** Setting the position for the marker*/
            markerOptions.position(latLng)

            /** This will be displayed on taping the marker*/
            markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)
            /** where we will store the list of all address.*/
            var mapAddressList: MutableList<Address?>? = null

            /** on below line we are creating and initializing a geo coder. */
            val geocoder = Geocoder(applicationContext)
            try {
                /** location name and adding that location to address list.*/
                mapAddressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                Log.e(javaClass.simpleName, "addresses1: $mapAddressList")
                /** For Android SDK < 33, the addresses list will be still obtained from the getFromLocation() method*/
                if (!mapAddressList.isNullOrEmpty()) {
                    val mapAddress: Address? = mapAddressList[0]
                    if (mapAddress != null) {
                        binding.group.visibility = View.VISIBLE
                        Log.e(javaClass.simpleName, "address: $mapAddress")
                        binding.locale.text = "Place : " + mapAddress.getAddressLine(0).toNullString()
                        binding.postalCode.text = "PostalCode : " + mapAddress.postalCode.toNullString()
                        binding.subLocality.text = "SubLocality : " + mapAddress.locality.toNullString()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            /** Clears the previously touched position*/
            mMap.clear()

            /** Animating to the touched position*/
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))

            /** Placing a marker on the touched position*/
            mMap.addMarker(markerOptions)
        }
    }
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->

                        requestPermissions()
                    }
                    .create()
                    .show()
            } else {

                requestPermissions()
            }
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        /** Provide an additional rationale to the user. This would happen if the user denied the
        request previously, but didn't check the "Don't ask again" checkbox.*/
        if (shouldProvideRationale) {
            Log.e(
                javaClass.simpleName,
                "Displaying permission rationale to provide additional context."
            )
            /** Request permission */
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        } else {
            Log.e(javaClass.simpleName, "Requesting permission")
            /** Request permission. It's possible this can be auto answered if device policy
            sets the permission in a given state or the user denied the permission
            previously and checked "Never ask again".*/
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    companion object {
        /**
         * Code used in requesting runtime permissions.
         */
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }



    override fun onLocationUpdate(latLng: LatLng) {
        Log.e(javaClass.simpleName, "onLocationUpdate: "+latLng )
        longitudeCurrent = latLng.longitude
        latitudeCurrent = latLng.latitude
    }

}