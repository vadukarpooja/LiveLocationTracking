package com.example.activityresultlauncher


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.activityresultlauncher.databinding.ActivityLocationBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.io.IOException


class LocationActivity : AppCompatActivity(), OnMapReadyCallback {


    private var latitudeCurrent = 0.0
    private var longitudeCurrent = 0.0
    private var mHandler: IncomingMessageHandler? = null

    private lateinit var mMap: GoogleMap
    private var mCurrLocationMarker: Marker? = null
    lateinit var binding: ActivityLocationBinding
    var locationUpdatesComponent:LocationUpdatesComponent? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mHandler = IncomingMessageHandler()
        /** Obtain the SupportMapFragment and get notified when the map is ready to be used.*/
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val searchLocation = findViewById<ImageView>(R.id.searchLocation)
        searchLocation.setOnClickListener {
            val intent = Intent(this,LocationTrackingActivity::class.java,)
            startActivity(intent)
        }


    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
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
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true

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
                    list.addAll(arrayListOf(latLng))
                    Log.e(javaClass.simpleName, "list: $list")
                    /** PolyLine Start Point Marker*/
                    mCurrLocationMarker= mMap.addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(
                                    list[0].latitude,
                                    list[0].longitude
                                )
                            )
                    )
                    /** PolyLine End Point Marker*/

                    /*  val lastIndex = list.size - 1
                    mMap.addMarker(MarkerOptions()
                         .position(LatLng(list[lastIndex].latitude,list[lastIndex].longitude)))*/
                    drawDashedPolyLine(mMap, list, R.color.black)

                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        list.clear()
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

                   startActivity(Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", this.packageName, null),
                    ))
                    //activityResultLauncher.launch(intent)

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
                     requestBackgroundLocationPermission()
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (resultCode == RESULT_OK) {
                Log.e(javaClass.simpleName, "onActivityResult: $RESULT_OK")
               // locationUpdatesComponent!!.requestLocationUpdates()

            } else if (resultCode == RESULT_CANCELED) {
                Log.e(javaClass.simpleName, "onActivityResult: $RESULT_CANCELED")
            }
        }
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
}





