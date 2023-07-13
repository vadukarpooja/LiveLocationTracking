package com.example.activityresultlauncher

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class ApplicationClass : Application() {
    lateinit var activity: AppCompatActivity
    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        activity = getactivity()
        context = getApplicationContext()
    }

    fun get(context: Context): ApplicationClass {
        return context.applicationContext as ApplicationClass
    }

    fun setActity(activity: AppCompatActivity) {
        this.activity = activity
    }

    fun getactivity(): AppCompatActivity {
        return this.activity
    }

}