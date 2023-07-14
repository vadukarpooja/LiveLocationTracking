package com.example.activityresultlauncher

import android.location.Location

interface LocationInterface {

    fun onLocationUpdate(location: Location?)
}