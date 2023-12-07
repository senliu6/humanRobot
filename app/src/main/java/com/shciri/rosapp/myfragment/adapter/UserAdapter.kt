package com.shciri.rosapp.myfragment.adapter

/**
 * 功能：
 * @author ：liudz
 * 日期：2023年12月05日
 */
// UserAdapter.kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hjq.shape.view.ShapeTextView
import com.shciri.rosapp.R
import com.shciri.rosapp.dmros.data.User

class UserAdapter(private val userList: List<User>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    interface OnItemClickListener {
        fun onEditClick(user: User,position: Int)
        fun onDeleteClick(user: User, position: Int)
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val editButton: ShapeTextView = itemView.findViewById(R.id.editButton)
        val deleteButton: ShapeTextView = itemView.findViewById(R.id.deleteButton)
        val passwordTextView: TextView = itemView.findViewById(R.id.passwordTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_manager, parent, false)
        return UserViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.usernameTextView.text = currentUser.username

        holder.editButton.setOnClickListener {
            listener.onEditClick(currentUser,position)
        }

        holder.deleteButton.setOnClickListener {
            listener.onDeleteClick(currentUser, position)
        }
        holder.passwordTextView.text = currentUser.password
    }

    override fun getItemCount() = userList.size
}
