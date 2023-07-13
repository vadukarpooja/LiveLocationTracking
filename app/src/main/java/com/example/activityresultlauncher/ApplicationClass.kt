package com.example.activityresultlauncher

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class ApplicationClass : Application() {
    private lateinit var activity: AppCompatActivity
    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        activity = getActivity()
        context = applicationContext
    }

    fun get(context: Context): ApplicationClass {
        return context.applicationContext as ApplicationClass
    }

    private fun getActivity(): AppCompatActivity {
        return this.activity
    }

}