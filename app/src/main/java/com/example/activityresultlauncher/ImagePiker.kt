package com.example.activityresultlauncher

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

internal class ImagePiker : AppCompatActivity() {
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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById<View>(R.id.btn) as Button
        img = findViewById<View>(R.id.image_c) as ImageView
        imgG = findViewById<View>(R.id.image_g) as ImageView
        btnG = findViewById<View>(R.id.btn_g) as Button
        location = findViewById<View>(R.id.imgLocation) as ImageView

        location!!.setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
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
}