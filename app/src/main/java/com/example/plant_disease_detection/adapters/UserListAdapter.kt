package com.example.plant_disease_detection.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plant_disease_detection.R
import com.example.plant_disease_detection.models.User
import com.example.plant_disease_detection.utils.UserManager

class UserListAdapter(
    private var users: List<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvUserName)
        val tvEmail: TextView = view.findViewById(R.id.tvUserEmail)
        val tvJoinDate: TextView = view.findViewById(R.id.tvJoinDate)
        val tvLastLogin: TextView = view.findViewById(R.id.tvLastLogin)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvRole: TextView = view.findViewById(R.id.tvRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.tvName.text = user.name
        holder.tvEmail.text = user.email
        holder.tvJoinDate.text = "Joined: ${user.joinDate}"
        holder.tvLastLogin.text = "Last login: ${user.lastLogin}"
        holder.tvStatus.text = if (user.isActive) "Active" else "Inactive"
        holder.tvStatus.setTextColor(
            holder.itemView.context.getColor(
                if (user.isActive) android.R.color.holo_green_dark
                else android.R.color.holo_red_dark
            )
        )
        
        // Set role badge
        holder.tvRole.text = user.role.name
        holder.tvRole.setBackgroundColor(
            holder.itemView.context.getColor(
                when (user.role) {
                    UserManager.UserRole.ADMIN -> android.R.color.holo_red_light
                    UserManager.UserRole.USER -> android.R.color.holo_blue_light
                }
            )
        )
        
        holder.itemView.setOnClickListener { onUserClick(user) }
    }

    override fun getItemCount() = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
} 