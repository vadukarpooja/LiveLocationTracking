package com.example.activityresultlauncher

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.activityresultlauncher.databinding.ActivityMainBinding
import java.io.File

class ImagePiker : AppCompatActivity() {
    private var requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private var isStorageImagePermitted = false
    private var isCameraAccessPermitted = false
    private var btn: Button? = null
    private var img: ImageView? = null
    private var imgG: ImageView? = null
    private var btnG: Button? = null
    private var location: ImageView? = null
    private var uriForCamera: Uri? = null
    private var layout: LinearLayout ? = null
    private val PERMISSION_REQUEST_FINE_LOCATION = 90
    val PERMISSION_REQUEST_BACKGROUND_LOCATION = 91
    lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val btnLayoutParams: ViewGroup.MarginLayoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        btn = findViewById<View>(R.id.btn) as Button
        img = findViewById<View>(R.id.image_c) as ImageView
        imgG = findViewById<View>(R.id.image_g) as ImageView
        btnG = findViewById<View>(R.id.btn_g) as Button
        location = findViewById<View>(R.id.imgLocation) as ImageView
        layout = findViewById<View>(R.id.layout)as LinearLayout
        val group:RadioGroup = RadioGroup(this)
        group.orientation = RadioGroup.HORIZONTAL
        val radioButton1 = RadioButton(this)
        group.addView(radioButton1)
        radioButton1.text = "Excellent"
        val radioButton2 = RadioButton(this)
        group.addView(radioButton2)
        radioButton2.text = "Good"
        val radioButton3 = RadioButton(this)
        group.addView(radioButton3)
        radioButton3.text = "Average"
        val radioButton4 = RadioButton(this)
        group.addView(radioButton4)
        radioButton4.text = "Poor"

        binding.btnAction.setOnClickListener {
            val type = "image/*"
            val filename = "/myPhoto.jpg"
            val mediaPath = Environment.getExternalStorageDirectory().toString() + filename
                // Create the new Intent using the 'Send' action.
                val share = Intent(Intent.ACTION_SEND)

                // Set the MIME type
                share.type = type

                // Create the URI from the media
                val media = File(mediaPath)
                val uri = Uri.fromFile(media)

                // Add the URI to the Intent.
                share.putExtra(Intent.EXTRA_STREAM, uriForCamera)

                // Broadcast the Intent.
                startActivity(Intent.createChooser(share, "Share to"));
        }

        layout?.addView(group)
        location!!.setOnClickListener {
          /*  if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        val builder =
                            AlertDialog.Builder(this)
                        builder.setTitle("This app needs background location access")
                        builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener {
                            requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                PERMISSION_REQUEST_BACKGROUND_LOCATION
                            )
                        }
                        builder.show()
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            val builder =
                                AlertDialog.Builder(this)
                            builder.setTitle("Functionality limited")
                            builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.")
                            builder.setPositiveButton(android.R.string.ok, null)
                            builder.setOnDismissListener {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri: Uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                // This will take the user to a page where they have to click twice to drill down to grant the permission
                                startActivity(intent)
                            }
                            builder.show()
                        }
                    }
                }
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION
                            *//*Manifest.permission.ACCESS_BACKGROUND_LOCATION*//*
                        ),
                        PERMISSION_REQUEST_FINE_LOCATION
                    )
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        // This will take the user to a page where they have to click twice to drill down to grant the permission
                        startActivity(intent)
                    }
                    builder.show()
                }
            }
*/
             val intent = Intent(this, LocationGeofenceActivity::class.java)
             startActivity(intent)
             finish()

        }

        if (!isStorageImagePermitted) {
            requestPermissionStorageImage()
        }
        btnG!!.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryActivityResultLauncher.launch(galleryIntent)
        }
        btn!!.setOnClickListener {
            if (isCameraAccessPermitted) {
                openCamera()

            } else {
                requestPermissionCameraAccess()
            }
        }
    }

    /**
     * gallery launcher
     */
    private var galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data!!.data
            imgG!!.setImageURI(imageUri)
        }
    }

    /**
     * Camera launcher
     */

    private fun openCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Test")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Captured by Test")
        uriForCamera =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForCamera)
        launcherForCamera.launch(cameraIntent)
    }

    private val launcherForCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.e(javaClass.simpleName, "onActivityResult: $uriForCamera")
                img!!.setImageURI(uriForCamera)
            }
        }

    /**
     * read storage media images start
     */
    private fun requestPermissionStorageImage() {
        if (ContextCompat.checkSelfPermission(
                this@ImagePiker,
                requiredPermissions[0]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(javaClass.simpleName, requiredPermissions[0] + "Granted")
            isStorageImagePermitted = true
            requestPermissionCameraAccess()
        } else {
            requiredPermissionsLauncherStorageImages.launch(requiredPermissions[0])
        }
    }

    private val requiredPermissionsLauncherStorageImages =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isStorageImagePermitted = if (isGranted) {
                Log.e(javaClass.simpleName, requiredPermissions[0] + "Granted")
                true
            } else {
                Log.e(javaClass.simpleName, requiredPermissions[0] + "Not Granted")
                false
            }
            requestPermissionCameraAccess()
        }

    /**
     * Camera Access
     */
    private fun requestPermissionCameraAccess() {
        if (ContextCompat.checkSelfPermission(
                this@ImagePiker,
                requiredPermissions[1]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(javaClass.simpleName, requiredPermissions[1] + "Granted")
            isStorageImagePermitted = true
            isCameraAccessPermitted = true
        } else {
            requestPermissionLauncherCameraAccess.launch(requiredPermissions[1])
        }
    }

    private val requestPermissionLauncherCameraAccess =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            isCameraAccessPermitted = if (isGranted) {
                Log.e(javaClass.simpleName, requiredPermissions[1] + "Granted")
                true
            } else {
                Log.e(javaClass.simpleName, requiredPermissions[1] + "Not Granted")
                false
            }
        }

    companion object{


    }
}