package com.example.plant_disease_detection

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.plant_disease_detection.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Register activity result launcher for gallery picker
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                // Launch DetectionActivity with the selected image
                val intent = Intent(this, DetectionActivity::class.java).apply {
                    putExtra(DetectionActivity.EXTRA_IMAGE_PATH, imageUri.toString())
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Failed to get image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupClickListeners() {
        binding.scanButton.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        binding.uploadButton.setOnClickListener {
            openGallery()
        }

        binding.searchInput.setOnClickListener {
            // TODO: Will implement search functionality
            Toast.makeText(this, "Search feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Already on home
                    true
                }
                R.id.navigation_history -> {
                    Toast.makeText(this, "History coming soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_community -> {
                    Toast.makeText(this, "Community feature coming soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.navigation_settings -> {
                    Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
} 