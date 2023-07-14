package com.example.activityresultlauncher


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap


class LocationActivity : AppCompatActivity(), OnMapReadyCallback {


    private var latitudeCurrent = 0.0
    private var longitudeCurrent = 0.0
    private var mHandler: IncomingMessageHandler? = null


    private lateinit var mMap: GoogleMap
    private var mCurrLocationMarker: Marker? = null
    private var locationUpdatesComponent: LocationUpdatesComponent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        mHandler = IncomingMessageHandler()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


        if ((ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
                    == PackageManager.PERMISSION_GRANTED
                    ) && (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
                    == PackageManager.PERMISSION_GRANTED)
        ) {

            val startServiceIntent = Intent(this, LocationUpdatesService::class.java)
            val messengerIncoming = Messenger(mHandler)
            startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
            startService(startServiceIntent)


        } else {
            checkLocationPermission()
        }


    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val markerOptions = MarkerOptions()
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        // Setting a click event handler for the map
        /*mMap.setOnMapClickListener { latLng -> // Creating a marker


            // Setting the position for the marker
            markerOptions.position(latLng)

            // Setting the title for the marker.
            // This will be displayed on taping the marker
            markerOptions.title(latLng.latitude.toString() + " : " + latLng.longitude)

            // Clears the previously touched position
            mMap.clear()

            // Animating to the touched position
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))

            // Placing a marker on the touched position
            mMap.addMarker(markerOptions)
        }*/

    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
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

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
        const val MESSENGER_INTENT_KEY = "msg-intent-key"
    }

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
    val list = ArrayList<LatLng>()
    @SuppressLint("HandlerLeak")
    inner class IncomingMessageHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                LocationUpdatesService.LOCATION_MESSAGE -> {
                    val obj = msg.obj as Location
                    latitudeCurrent = obj.latitude
                    longitudeCurrent = obj.longitude
                    val latLng = LatLng(obj.latitude, obj.longitude)
                    list.addAll(arrayListOf(latLng))
                    Log.e(javaClass.simpleName, "list: $list")
                    mCurrLocationMarker = mMap.addMarker(MarkerOptions().position(LatLng(list[0].latitude,
                        list[0].longitude)))
                    val lastIndex = list.lastIndex
                    //mCurrLocationMarker = mMap.addMarker(MarkerOptions().position(LatLng(list[lastIndex].latitude,list[lastIndex].longitude)))
                    drawDashedPolyLine(mMap,list,R.color.black)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler = null
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
                        val startServiceIntent = Intent(this, LocationUpdatesService::class.java)
                        val messengerIncoming = Messenger(mHandler)
                        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
                        startService(startServiceIntent)
                    } else {
                        requestLocationPermission()
                    }

                } else if (!shouldShowRequestPermissionRationale(permissions[0])) {

                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", this.packageName, null),
                    )
                    activityResultLauncher.launch(intent)

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
                        val startServiceIntent = Intent(this, LocationUpdatesService::class.java)
                        val messengerIncoming = Messenger(mHandler)
                        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
                        startService(startServiceIntent)
                        Toast.makeText(
                            this,
                            "Granted Background Location Permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }

    private var activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            Log.e(javaClass.simpleName, "RESULT_OK: $RESULT_OK")
            val startServiceIntent = Intent(this, LocationUpdatesService::class.java)
            val messengerIncoming = Messenger(mHandler)
            startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
            startService(startServiceIntent)

        } else if (result.resultCode == RESULT_CANCELED) {
            Log.e(javaClass.simpleName, "RESULT_CANCELED$RESULT_CANCELED")
            requestLocationPermission()
        }

    }

    private fun drawDashedPolyLine(mMap: GoogleMap, listOfPoints: ArrayList<LatLng>, color: Int) {
        /* Boolean to control drawing alternate lines */
        Log.e(javaClass.simpleName, "listOfPoints: $listOfPoints")
        var added = false
        for (i in 0 until listOfPoints.size - 1) {
            /* Get distance between current and next point */
                added = if (!added) {
                    mMap.addPolyline(
                        PolylineOptions()
                            .add(listOfPoints[i])
                            .add(listOfPoints[i + 1])
                            .color(color)
                            .width(15f)
                            .endCap(RoundCap())
                    )
                    true
                } else {
                    false
                }
            }
        }
    }
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (resultCode == RESULT_OK) {
                val startServiceIntent = Intent(this, LocationUpdatesService::class.java)
                val messengerIncoming = Messenger(mHandler)
                startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)
                startService(startServiceIntent)

               // locationUpdatesComponent.displayLocationSettingsRequest(this, this)

            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }*/
    /* override fun onLocationUpdate(location: Location?) {
         if (location!=null){
             latitudeCurrent = location.latitude
             longitudeCurrent = location.longitude
         }

         Log.e(javaClass.simpleName, "LatLong" +
                 ": $location")

         if (mCurrLocationMarker != null) {
             mCurrLocationMarker!!.remove()
         }
         //Place current location marker
         val latLng = LatLng(latitudeCurrent,longitudeCurrent)
         val markerOptions = MarkerOptions()
         markerOptions.position(latLng)
         markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
         mCurrLocationMarker = mMap.addMarker(markerOptions)
         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
     }*/


