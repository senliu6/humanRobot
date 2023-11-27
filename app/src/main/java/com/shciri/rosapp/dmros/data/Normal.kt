package com.shciri.rosapp.dmros.data

/**
 * 功能：一些常见的全局数据
 * @author ：liudz
 * 日期：2023年11月13日
 */

//周列表
object WeekString {
    const val EVERY_DAY = "1,2,3,4,5,6,7"
    const val WORK_DAY = "2,3,4,5,6"
    const val WEEK_DAY = "1,7"
    const val ONE_THREE = "2,4,6"
    const val TWO_FOUR = "3,5,7"
}
//IP设置
object Settings {
    const val ROBOT_IP: String = "robot_ip"
}

//用户管理
data class User(val username: String, val password: String)

object UserList {
    val users: List<User> = listOf(
        User("Admin", "1"),
        User("User", "66"),
        User("Test", "88")
    )

    fun isValidUser(username: String, password: String): Boolean {
        return users.any { it.username == username && it.password == password }
    }

    val array: Array<String> = users.map { it.username }.toTypedArray()
}