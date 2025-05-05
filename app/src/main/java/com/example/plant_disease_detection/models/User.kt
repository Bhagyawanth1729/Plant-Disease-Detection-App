package com.example.plant_disease_detection.models

import com.example.plant_disease_detection.utils.UserManager

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserManager.UserRole,
    val joinDate: String,
    val lastLogin: String,
    val isActive: Boolean = true
) 