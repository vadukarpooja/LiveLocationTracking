package com.example.activityresultlauncher

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.activityresultlauncher.databinding.ActivityLocationTrackingBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

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
                            binding.locale.text = "Place : " + address.getAddressLine(0)
                            binding.subLocality.text =
                                "SubLocality : " + address.subLocality
                            binding.postalCode.text =
                                "PostalCode : " + address.postalCode
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Utils.getLocationData(this,this)
                      } else {
                        requestLocationPermission()
                    }

                } else if (!shouldShowRequestPermissionRationale(permissions[0])) {

                    startActivity(Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", this.packageName, null),
                    ))

                } else {
                    requestLocationPermission()
                }
                return
            }

            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {

                if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Utils.getLocationData(this,this)
                        Toast.makeText(
                            this,
                            "Granted Background Location Permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    requestBackgroundLocationPermission()
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }
    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }
    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
    }
    @SuppressLint("SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        checkLocationPermission()
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        Utils.getLocationData(this,this)
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
                        binding.locale.text = "Place : " + mapAddress.getAddressLine(0)
                        binding.postalCode.text = "PostalCode : " + mapAddress.postalCode
                        binding.subLocality.text = "SubLocality : " + mapAddress.locality
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

                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {

                requestLocationPermission()
            }
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }


    companion object {
        /**
         * Code used in requesting runtime permissions.
         */
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }



    override fun onLocationUpdate(latLng: LatLng) {
        Log.e(javaClass.simpleName, "onLocationUpdate: $latLng")
        mMap.clear()

        val mp = MarkerOptions()

        mp.position(latLng)

        mp.title("my position")

        mMap.addMarker(mp)

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
               LatLng(latLng.latitude,latLng.longitude), 16f
            )
        )
        longitudeCurrent = latLng.longitude
        latitudeCurrent = latLng.latitude
    }

}