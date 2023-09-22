package com.example.activityresultlauncher

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices.getGeofencingClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class LocationGeofenceActivity : AppCompatActivity(), OnMapReadyCallback, OnMapLongClickListener {
    lateinit var mMap: GoogleMap
    private val GEOFENCE_RADIUS = 35f
    private val GEOFENCE_ID = "SOME_GEOFENCE_ID"
    lateinit var geofencingClient: GeofencingClient
    lateinit var geofenceHelper: GeofenceHelper
    private var mCurrLocationMarker: Marker? = null
    private var latitudeCurrent = 0.0
    private var longitudeCurrent = 0.0
    lateinit var mHandler: IncomingMessageHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_permission)

        /** Obtain the SupportMapFragment and get notified when the map is ready to be used.*/
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        geofenceHelper = GeofenceHelper(this);
        mHandler = IncomingMessageHandler()
        geofencingClient = getGeofencingClient(this)
        checkPermission()
    }
    private fun checkPermission() {
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
            startServiceIntent.putExtra(LocationActivity.MESSENGER_INTENT_KEY, messengerIncoming)
            startService(startServiceIntent)
        } else {
            checkLocationPermission()
        }


    }
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
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
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
           MY_PERMISSIONS_REQUEST_LOCATION
        )
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.e(javaClass.simpleName, "onMapReady: " + LatLng(latitudeCurrent, longitudeCurrent))
        mMap.setOnMapLongClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true
    }


    override fun onMapLongClick(latLng: LatLng) {

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
                        //mMap.isMyLocationEnabled = true
                        val startServiceIntent = Intent(this, LocationUpdatesService::class.java)
                        val messengerIncoming = Messenger(mHandler)
                        startServiceIntent.putExtra(
                            LocationActivity.MESSENGER_INTENT_KEY,
                            messengerIncoming
                        )
                        startService(startServiceIntent)
                    } else {
                        requestLocationPermission()
                        Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()

                    }

                } else if (!shouldShowRequestPermissionRationale(permissions[0])) {

                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", this.packageName, null)
                        )
                    )


                } else {
                    requestLocationPermission()
                }
                return
            }
            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                           // mMap.isMyLocationEnabled = true
                            val startServiceIntent =
                                Intent(this, LocationUpdatesService::class.java)
                            val messengerIncoming = Messenger(mHandler)
                            startServiceIntent.putExtra(
                                LocationActivity.MESSENGER_INTENT_KEY,
                                messengerIncoming
                            )
                            startService(startServiceIntent)
                            Toast.makeText(
                                this,
                                "Granted Background Location Permission",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                     else {
                        Toast.makeText(this, "permission Background denied", Toast.LENGTH_LONG).show()
                    }
                    return
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        list.clear()
       // mHandler = null

    }


    private fun drawDashedPolyLine(mMap: GoogleMap, listOfPoints: ArrayList<LatLng>, color: Int) {
        /* Boolean to control drawing alternate lines */
        Log.e(javaClass.simpleName, "listOfPoints: $listOfPoints")
        var added = false
        for (i in 0 until listOfPoints.size - 1) {
            /** Get distance between current and next point */
            added = if (!added) {
                mMap.addPolyline(
                    PolylineOptions()
                        .add(listOfPoints[i])
                        .add(listOfPoints[i + 1])
                        .color(color)
                        .width(15f)
                        .geodesic(true)
                )
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            listOfPoints[i].latitude, listOfPoints[i].longitude
                        ), 18f
                    )
                )
                true
            } else {
                false
            }
        }
    }

    private fun handleMapLongClick(latLng: LatLng) {
        Log.e(javaClass.simpleName, "handleMapLongClick: $latLng")
        mMap.clear()
        addMarker(latLng)
        addCircle(latLng, GEOFENCE_RADIUS)
        addGeofence(latLng, GEOFENCE_RADIUS)
    }

    private fun addGeofence(latLng: LatLng, radius: Float) {
        Log.e(javaClass.simpleName, "addGeofence: $latLng")
        val geofence = geofenceHelper.getGeofence(
            GEOFENCE_ID, latLng, 200f,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT,
            10000
        )
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        Log.e(javaClass.simpleName, "geofencingRequest: $geofencingRequest")
        val pendingIntent = geofenceHelper.getPendingIntent()
        Log.e(javaClass.simpleName, "pendingIntent1: $pendingIntent")
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
                geofencingClient.addGeofences(
                    geofenceHelper.getGeofencingRequest(geofence),
                    geofenceHelper.getPendingIntent()
                ).addOnSuccessListener {
                    Log.e(javaClass.simpleName, "onSuccess: Added...")
                }
                    .addOnFailureListener { e ->
                        val errorMessage = geofenceHelper.getErrorString(e)
                        Toast.makeText(this, "Background Location Permission denied", Toast.LENGTH_LONG)
                            .show()
                        Log.e(javaClass.simpleName, "onFailure: $errorMessage")
                    }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                ) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
                    )
                }
            }
        } else {
            mMap.isMyLocationEnabled = true
            geofencingClient.addGeofences(
                geofenceHelper.getGeofencingRequest(geofence),
                geofenceHelper.getPendingIntent()
            ).addOnSuccessListener {
                Log.e(javaClass.simpleName, "onSuccess: Added...")
            }
                .addOnFailureListener { e ->
                    val errorMessage = geofenceHelper.getErrorString(e)
                    Toast.makeText(this, "Background Location Permission denied", Toast.LENGTH_LONG)
                        .show()
                    Log.e(javaClass.simpleName, "onFailure: $errorMessage")
                }
        }
    }

    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng)
        mMap.addMarker(markerOptions)
    }

    private fun addCircle(latLng: LatLng, radius: Float) {
        val circleOptions = CircleOptions()
        circleOptions.center(latLng)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4f)
        mMap.addCircle(circleOptions)
    }


    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
        const val MESSENGER_INTENT_KEY = "msg-intent-key"
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
                    handleMapLongClick(latLng)
                    list.addAll(arrayListOf(latLng))
                    Log.e(javaClass.simpleName, "list: $list")
                    /** PolyLine Start Point Marker*/
                    mCurrLocationMarker = mMap.addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(
                                    list[0].latitude,
                                    list[0].longitude
                                )
                            )
                    )
                    /** PolyLine End Point Marker*/
                    drawDashedPolyLine(mMap, list, R.color.black)

                }
            }
        }

    }

}