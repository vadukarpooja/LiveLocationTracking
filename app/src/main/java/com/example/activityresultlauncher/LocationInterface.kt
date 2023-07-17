package com.example.activityresultlauncher

import android.location.Location
import com.google.android.gms.maps.model.LatLng

interface LocationInterface {

    fun onLocationUpdate(latLng: LatLng)
}