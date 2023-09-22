package com.example.activityresultlauncher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class Extension {
    /**Value from EditText
     */
    /**Use */
    /** val name = etName.value*/
    val EditText.value
        get() = text?.toString() ?: ""



   /** Start Activity
    */
    fun Activity.startActivity(
        cls: Class<*>,
        finishCallingActivity: Boolean = true,
        block: (Intent.() -> Unit)? = null
    ) {
        val intent = Intent(this, cls)
        block?.invoke(intent)
        startActivity(intent)
        if (finishCallingActivity) finish()
    }

    /**Use Start Activity
     * startActivity(MainActivity::class.java) // Without Intent modification
     *     startActivity(MainActivity::class.java) {
     *         // You can access the intent object in this block
     *         putExtra("key", "value")
     *     }*/


    /** Check Network */
    private fun Context.isNetworkAvailable(): Boolean {
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        return if (capabilities != null) {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else false
    }

    fun Fragment.isNetworkAvailable() = requireContext().isNetworkAvailable()
    /** Use
     * if (isNetworkAvailable()) {
     *         // Called when network is available
     *     } else {
     *         // Called when network not available
     *     }*/




    /**
     * Check Permission
     * */
    fun Context.isPermissionGranted(permission: String) = run {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
    /**Use
     * if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
     *     // Block runs if permission is granted
     * } else {
     *     // Ask for permission
     * }*/
}