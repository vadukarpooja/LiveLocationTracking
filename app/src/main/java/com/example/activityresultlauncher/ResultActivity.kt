package com.example.activityresultlauncher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.activityresultlauncher.databinding.ActivityResultBinding


class ResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultBinding
    val RESULTCODE = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val fragInfo = TestFragment()
        binding.btnActionFragment.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("email", binding.email.text.toString())
            fragInfo.arguments = bundle
            fragmentTransaction.replace(R.id.frameLayout, fragInfo)
            fragmentTransaction.addToBackStack("email")
            fragmentTransaction.commit()
        }

         binding.btnAction.setOnClickListener {
            val intent = Intent(this,TestActivity::class.java)
            startActivityForResult(intent,RESULTCODE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULTCODE && resultCode == RESULT_OK) {
            val email = data?.getStringExtra("email")
            binding.email.setText(email)
            Log.e(javaClass.simpleName, "onActivityResult: $email")
        }
    }


}