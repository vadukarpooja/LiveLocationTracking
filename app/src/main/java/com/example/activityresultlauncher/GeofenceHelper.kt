package com.example.activityresultlauncher

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.example.activityresultlauncher.GeofenceBroadcastReceiver
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng

class GeofenceHelper(base: Context?) : ContextWrapper(base) {
    private lateinit var pendingIntent: PendingIntent
    fun getGeofencingRequest(geofence: Geofence?): GeofencingRequest {
        return GeofencingRequest.Builder().addGeofence(geofence!!)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).build()
    }

    fun getGeofence(ID: String, latLng: LatLng, radius: Float, transitionTypes: Int,setNotificationResponsiveness:Int): Geofence {
        return Geofence.Builder().setCircularRegion(latLng.latitude, latLng.longitude, radius).setRequestId(ID
            ).setTransitionTypes(transitionTypes).setLoiteringDelay(5000)
            .setNotificationResponsiveness(setNotificationResponsiveness)
            .setExpirationDuration(Geofence.NEVER_EXPIRE).build()
    }

    fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        return  PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    fun getErrorString(e: Exception): String? {
        if (e is ApiException) {
            when (e.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return "GEOFENCE_TOO_MANY_GEOFENCES"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return "GEOFENCE_TOO_MANY_PENDING_INTENTS"
            }
        }
        return e.localizedMessage
    }

    companion object {
        private const val TAG = "GeofenceHelper"
    }
}