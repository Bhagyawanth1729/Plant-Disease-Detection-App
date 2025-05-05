package com.example.plant_disease_detection.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object UserManager {
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()
    private var users = mutableMapOf<String, UserData>()
    
    // Admin credentials
    private const val ADMIN_EMAIL = "admin@plantguard.com"
    private const val ADMIN_PASSWORD = "admin123"

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        loadUsers()
        
        // Ensure admin account exists
        if (!users.containsKey(ADMIN_EMAIL)) {
            users[ADMIN_EMAIL] = UserData(
                email = ADMIN_EMAIL,
                password = ADMIN_PASSWORD,
                fullName = "Admin",
                role = UserRole.ADMIN,
                joinDate = "2024-01-01",
                lastLogin = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date())
            )
            saveUsers()
        }
        Log.d("UserManager", "Initialized with ${users.size} users")
    }

    private fun loadUsers() {
        val usersJson = prefs.getString("users", null)
        if (usersJson != null) {
            val type = object : TypeToken<Map<String, UserData>>() {}.type
            users = gson.fromJson(usersJson, type)
            Log.d("UserManager", "Loaded ${users.size} users from storage")
        }
    }

    private fun saveUsers() {
        val usersJson = gson.toJson(users)
        prefs.edit().putString("users", usersJson).apply()
        Log.d("UserManager", "Saved ${users.size} users to storage")
    }

    fun addUser(email: String, password: String, fullName: String) {
        if (email != ADMIN_EMAIL) {  // Prevent overwriting admin account
            users[email] = UserData(
                email = email,
                password = password,
                fullName = fullName,
                role = UserRole.USER,
                joinDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date()),
                lastLogin = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date())
            )
            saveUsers()
            Log.d("UserManager", "Added new user: $email")
            Log.d("UserManager", "Current users: ${users.keys}")
        }
    }

    fun validateUser(email: String, password: String): Boolean {
        val user = users[email]
        Log.d("UserManager", "Login attempt for: $email")
        Log.d("UserManager", "User found: ${user != null}")
        Log.d("UserManager", "Current users: ${users.keys}")
        
        if (user?.password == password) {
            // Update last login time
            users[email] = user.copy(
                lastLogin = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(java.util.Date())
            )
            saveUsers()
            Log.d("UserManager", "Login successful for: $email")
            return true
        }
        Log.d("UserManager", "Login failed for: $email")
        return false
    }

    fun isAdmin(email: String): Boolean {
        return users[email]?.role == UserRole.ADMIN
    }

    fun getAllUsers(): List<UserData> {
        Log.d("UserManager", "Getting all users. Count: ${users.size}")
        Log.d("UserManager", "Users: ${users.keys}")
        return users.values.toList()
    }

    fun userExists(email: String): Boolean {
        return users.containsKey(email)
    }

    enum class UserRole {
        ADMIN, USER
    }

    data class UserData(
        val email: String,
        val password: String,
        val fullName: String,
        val role: UserRole = UserRole.USER,
        val joinDate: String = "",
        val lastLogin: String = "",
        val isActive: Boolean = true
    )
} 