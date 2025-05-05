package com.example.plant_disease_detection

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plant_disease_detection.adapters.UserListAdapter
import com.example.plant_disease_detection.databinding.ActivityAdminBinding
import com.example.plant_disease_detection.models.User
import com.example.plant_disease_detection.utils.UserManager

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var userAdapter: UserListAdapter
    private var allUsers = listOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupSearch()
        loadUsersList()
    }

    private fun setupUI() {
        title = "User Management"
        
        // Initialize RecyclerView
        userAdapter = UserListAdapter(emptyList()) { user ->
            showUserDetails(user)
        }
        
        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(this@AdminActivity)
            adapter = userAdapter
        }
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterUsers(s?.toString() ?: "")
            }
        })
    }

    private fun filterUsers(query: String) {
        val filteredUsers = if (query.isEmpty()) {
            allUsers
        } else {
            allUsers.filter { user ->
                user.name.contains(query, ignoreCase = true) ||
                user.email.contains(query, ignoreCase = true)
            }
        }
        updateUsersList(filteredUsers)
    }

    private fun loadUsersList() {
        // Get all users including admin from UserManager
        val registeredUsers = UserManager.getAllUsers()
        allUsers = registeredUsers.map { userData ->
            User(
                id = userData.email, // Using email as ID since it's unique
                email = userData.email,
                name = userData.fullName,
                role = userData.role,
                joinDate = userData.joinDate,
                lastLogin = userData.lastLogin,
                isActive = userData.isActive
            )
        }
        updateUsersList(allUsers)
    }

    private fun updateUsersList(users: List<User>) {
        binding.tvUserCount.text = "Total Users: ${users.size}"
        
        if (users.isEmpty()) {
            binding.tvNoUsers.visibility = View.VISIBLE
            binding.tvNoUsers.text = "No users found"
            binding.recyclerViewUsers.visibility = View.GONE
        } else {
            binding.tvNoUsers.visibility = View.GONE
            binding.recyclerViewUsers.visibility = View.VISIBLE
            userAdapter.updateUsers(users)
        }
    }

    private fun showUserDetails(user: User) {
        // Show user details in a toast
        Toast.makeText(
            this,
            """
            User Details:
            Name: ${user.name}
            Email: ${user.email}
            Role: ${user.role}
            Joined: ${user.joinDate}
            Last Login: ${user.lastLogin}
            Status: ${if (user.isActive) "Active" else "Inactive"}
            """.trimIndent(),
            Toast.LENGTH_LONG
        ).show()
        // TODO: Implement a detailed view or dialog for user management
    }
} 