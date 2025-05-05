package com.example.plant_disease_detection

import android.app.Application
import com.example.plant_disease_detection.utils.UserManager

class PlantGuardApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        UserManager.initialize(this)
    }
} 