package com.test.issuemediasessioncompat

import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.test.issuemediasessioncompat.media.MediaService
import com.test.issuemediasessioncompat.media.MediaSessionConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val componentName = ComponentName(applicationContext, MediaService::class.java)
        val mediaSessionConnection = MediaSessionConnection.getInstance(applicationContext, componentName)
    }
}