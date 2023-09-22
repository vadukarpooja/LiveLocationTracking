package com.example.activityresultlauncher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.activityresultlauncher.databinding.ActivityTestBinding


class TestActivity : AppCompatActivity() {
    val RESULTCODE:Int = 12
    lateinit var binding: ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = Intent(this, ImagePiker::class.java)
        startActivityForResult(intent, RESULTCODE)

        binding.btnSent.setOnClickListener {
            val intent2 = Intent()
            intent2.putExtra("email", "feni@gmail.com")
            setResult(RESULT_OK, intent2)
            finish()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val email2 = data?.getStringExtra("email2")
            binding.email.text = email2
            Log.e(javaClass.simpleName, "onActivityResult2: $email2")
        }
    }



}