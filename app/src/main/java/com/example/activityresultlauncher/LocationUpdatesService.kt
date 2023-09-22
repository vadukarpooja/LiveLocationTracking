package com.example.activityresultlauncher


import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.location.Location
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log



class LocationUpdatesService:JobService(), LocationUpdatesComponent.ILocationProvider {
    private var mActivityMessenger: Messenger? = null
    private var locationUpdatesComponent: LocationUpdatesComponent? = null

    override fun onStartJob(params: JobParameters): Boolean {
        Log.e(TAG, "onStartJob....")
        /*Utils.scheduleJob(getApplicationContext(), LocationUpdatesService.class);*/
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.e(TAG, "onStopJob....")
        locationUpdatesComponent!!.onStop()
        return false
    }

    override fun onCreate() {
        Log.e(TAG, "created...............")
        locationUpdatesComponent = LocationUpdatesComponent( this)
        locationUpdatesComponent?.onCreate(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand Service started")
        mActivityMessenger =
            intent.getParcelableExtra(LocationGeofenceActivity.MESSENGER_INTENT_KEY)
        /** hey request for location updates*/
        locationUpdatesComponent?.onStart()
        return START_STICKY


    }

    override fun onRebind(intent: Intent) {
        /** Called when a client (MainActivity in case of this sample) returns to the foreground
        and binds once again with this service. The service should cease to be a foreground
        service when that happens.
         */
        Log.e(TAG, "in onRebind()")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.e(TAG, "Last client unbound from service")
        return true /** Ensures onRebind() is called when a client re-binds.*/
    }

    override fun onDestroy() {
       locationUpdatesComponent!!.onStop()
    }

    /**
     * send message by using messenger
     *
     * @param messageID
     */
    private fun sendMessage(messageID: Int, location: Location) {
      /**  If this service is launched by the JobScheduler, there's no callback Messenger. It
        only exists when the MainActivity calls startService() with the callback in the Intent.*/
        if (mActivityMessenger == null) {
            Log.e(TAG, "Service is bound, not started. There's no callback to send a message to.")
            return
        }
        val m = Message.obtain()
        m.what = messageID
        m.obj = location
        try {
            mActivityMessenger!!.send(m)
        } catch (e: RemoteException) {
            Log.e(TAG, "Error passing service object back to activity.")
        }
    }



    companion object {
        private val TAG = LocationUpdatesService::class.java.simpleName
        const val LOCATION_MESSAGE = 9999
    }

    override fun onLocationUpdate(location: Location?) {
        if (location != null) {
            sendMessage(LOCATION_MESSAGE, location)
        }
    }
}