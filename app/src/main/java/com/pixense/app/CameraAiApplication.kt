package com.pixense.app

import android.app.Application

class CameraAiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        /*try {
            CameraCaptureService.start(this)
            Log.d("CameraAiApplication", "CameraCaptureService auto-started")
        } catch (e: Exception) {
            Log.w("CameraAiApplication", "Background service will start after runtime permissions are granted", e)
        }*/
    }
}
