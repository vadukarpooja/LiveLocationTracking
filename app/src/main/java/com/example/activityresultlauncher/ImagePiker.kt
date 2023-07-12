package com.example.activityresultlauncher

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

internal class ImagePiker : AppCompatActivity() {
    var required_permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    var is_storage_image_permitted = false
    var is_camera_access_permitted = false
    var TAG = "Permission"
    var btn: Button? = null
    var img: ImageView? = null
    var imgG: ImageView? = null
    var btnG: Button? = null
    var uri_for_camera: Uri? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById<View>(R.id.btn) as Button
        img = findViewById<View>(R.id.image_c) as ImageView
        imgG = findViewById<View>(R.id.image_g) as ImageView
        btnG = findViewById<View>(R.id.btn_g) as Button
        if (!is_storage_image_permitted) {
            requestPermissionStorageImage()
        }
        btnG!!.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryActivityResultLauncher.launch(galleryIntent)
        }
        btn!!.setOnClickListener {
            if (is_camera_access_permitted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    openCamera()
                }
            } else {
                requestPermissionCameraAccess()
            }
        }
    }

    /**
     * gallery launcher
     */
    var galleryActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val image_uri = result.data!!.data
            imgG!!.setImageURI(image_uri)
        }
    }

    /**
     * Camera launcher
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    fun openCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Test")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Captured by Test")
        uri_for_camera =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri_for_camera)
        launcher_for_camera.launch(cameraIntent)
    }

    private val launcher_for_camera =
        registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.e(TAG, "onActivityResult: $uri_for_camera")
                img!!.setImageURI(uri_for_camera)
            }
        }

    /**
     * read storage media images start
     */
    fun requestPermissionStorageImage() {
        if (ContextCompat.checkSelfPermission(
                this@ImagePiker,
                required_permissions[0]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, required_permissions[0] + "Granted")
            is_storage_image_permitted = true
            requestPermissionCameraAccess()
        } else {
            required_permissions_launcher_storage_images.launch(required_permissions[0])
        }
    }

    private val required_permissions_launcher_storage_images =
        registerForActivityResult<String, Boolean>(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            is_storage_image_permitted = if (isGranted) {
                Log.e(TAG, required_permissions[0] + "Granted")
                true
            } else {
                Log.e(TAG, required_permissions[0] + "Not Granted")
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
                required_permissions[1]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, required_permissions[1] + "Granted")
            is_storage_image_permitted = true
            is_camera_access_permitted = true
        } else {
            request_permission_launcher_camera_access.launch(required_permissions[1])
        }
    }

    private val request_permission_launcher_camera_access =
        registerForActivityResult<String, Boolean>(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            is_camera_access_permitted = if (isGranted) {
                Log.e(TAG, required_permissions[1] + "Granted")
                true
            } else {
                Log.e(TAG, required_permissions[1] + "Not Granted")
                false
            }
        }
}