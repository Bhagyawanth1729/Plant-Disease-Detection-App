package com.example.plant_disease_detection

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plant_disease_detection.utils.UserManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signUpLink: View
    private lateinit var backButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        signUpLink = findViewById(R.id.signUpLink)
        backButton = findViewById(R.id.backButton)
    }

    private fun setupClickListeners() {
        loginButton.setOnClickListener { validateAndLogin() }
        signUpLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun validateAndLogin() {
        // Reset errors
        emailInputLayout.error = null
        passwordInputLayout.error = null

        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        when {
            TextUtils.isEmpty(email) -> {
                emailInputLayout.error = "Please enter your email"
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailInputLayout.error = "Please enter a valid email address"
                return
            }
            TextUtils.isEmpty(password) -> {
                passwordInputLayout.error = "Please enter your password"
                return
            }
            else -> handleLogin(email, password)
        }
    }

    private fun handleLogin(email: String, password: String) {
        if (UserManager.validateUser(email, password)) {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            
            // Redirect based on user type
            val intent = if (UserManager.isAdmin(email)) {
                Intent(this, AdminActivity::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
        }
    }
} 