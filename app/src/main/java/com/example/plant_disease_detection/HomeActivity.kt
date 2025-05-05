package com.example.plant_disease_detection

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.plant_disease_detection.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        
        // Make status bar transparent and extend content behind it
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up button clicks with debug logging and visual feedback
        setupButtons()

        // Make buttons visible immediately
        binding.loginButton.alpha = 1f
        binding.signupButton.alpha = 1f

        // Start animations for other elements
        startAnimations()
    }

    private fun setupButtons() {
        binding.loginButton.setOnClickListener { view ->
            Log.d(TAG, "Login button clicked")
            view.isEnabled = false
            Toast.makeText(this, "Opening Login...", Toast.LENGTH_SHORT).show()
            try {
                startActivity(Intent(this, LoginActivity::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Error starting LoginActivity", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
            view.isEnabled = true
        }

        binding.signupButton.setOnClickListener { view ->
            Log.d(TAG, "Signup button clicked")
            view.isEnabled = false
            Toast.makeText(this, "Opening Sign Up...", Toast.LENGTH_SHORT).show()
            try {
                startActivity(Intent(this, SignupActivity::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Error starting SignupActivity", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
            view.isEnabled = true
        }
    }

    private fun startAnimations() {
        val duration = 1500L
        val delay = 300L

        // Welcome text animation
        createFadeInAnimator(binding.welcomeText, duration, delay).start()

        // Plant text animation
        createFadeInAnimator(binding.plantText, duration, delay * 2).start()

        // World text animation
        createFadeInAnimator(binding.worldText, duration, delay * 3).start()

        // Tagline animation
        createFadeInAnimator(binding.taglineText, duration, delay * 4).start()

        // Start plant illustration animation
        startPlantAnimation()
    }

    private fun createFadeInAnimator(view: View, duration: Long, startDelay: Long): AnimatorSet {
        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        fadeIn.duration = duration
        fadeIn.interpolator = DecelerateInterpolator()

        return AnimatorSet().apply {
            play(fadeIn)
            this.startDelay = startDelay
        }
    }

    private fun startPlantAnimation() {
        val translateY = ObjectAnimator.ofFloat(binding.plantIllustration, "translationY", 0f, -20f, 0f)
        translateY.duration = 3000
        translateY.repeatCount = ObjectAnimator.INFINITE
        translateY.repeatMode = ObjectAnimator.REVERSE
        translateY.interpolator = DecelerateInterpolator()
        translateY.start()
    }
} 