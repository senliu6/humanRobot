package com.shciri.rosapp.dmros.tool

/**
 * 功能：用户管理 数据库操作
 * @author ：liudz
 * 日期：2023年12月05日
 */

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import com.shciri.rosapp.RCApplication
import com.shciri.rosapp.dmros.data.User

object UserRepository {

    fun addUser(user: User): Boolean {
        return if (isUsernameExist(user.username)) {
            false
        } else {
            val values = ContentValues().apply {
                put("username", user.username)
                put("password", user.password)
            }
            RCApplication.db.insert("user", null, values)
            true
        }
    }

    fun getAllUsers(): MutableList<User> {
        val userList = mutableListOf<User>()
        val cursor: Cursor = RCApplication.db.query(
            "user",
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow("username"))
                val password = getString(getColumnIndexOrThrow("password"))
                userList.add(User(name, password))
            }
        }
        cursor.close()
        return userList
    }

    fun changePassword(username: String, newPassword: String) {
        val values = ContentValues().apply {
            put("password", newPassword)
        }
        RCApplication.db.update(
            "user",
            values,
            "${"username"}=?",
            arrayOf(username)
        )

    }

    fun deleteUser(username: String) {
        RCApplication.db.delete("user", "${"username"}=?", arrayOf(username))
    }

    @SuppressLint("Recycle")
    fun isUsernameExist(username: String): Boolean {
        val cursor = RCApplication.db.query(
            "user",
            null,
            "${"username"}=?",
            arrayOf(username),
            null,
            null,
            null
        )
        val isExist = cursor.count > 0
        cursor.count
        return isExist
    }

    //获取用户名列表
    fun getUserNameArray(): Array<String> {
        return getAllUsers().map { it.username }.toTypedArray()
    }

    //获取密码是否正确
    fun isValidUser(username: String, password: String): Boolean {
        return getAllUsers().any { it.username == username && it.password == password }
    }
}

