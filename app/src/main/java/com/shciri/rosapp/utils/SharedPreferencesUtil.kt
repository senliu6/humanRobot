package com.shciri.rosapp.utils

/**
 * 功能：机器人常用设置类保存
 * @author ：liudz
 * 日期：2023年11月08日
 */
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shciri.rosapp.server.AlarmService
import java.lang.reflect.Type

class SharedPreferencesUtil {

    companion object {
        // 任务
        private const val ALARM_PREFERENCES_NAME = "ALARM_LIST"

        //任务列表
        private const val KEY_ALARM_LIST = "key_alarm_list"

        //常用设置
        private const val APP_SET_NAME = "APP_SET"

        //用户名前缀
        private const val KEY_USERNAME_PREFIX = "username_"

        //密码前缀
        private const val KEY_PASSWORD_PREFIX = "password_"


        // 提取 SharedPreferences 对象为私有属性
        private fun getAlarmPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(ALARM_PREFERENCES_NAME, Context.MODE_PRIVATE)
        }

        private fun getAppSetPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(APP_SET_NAME, Context.MODE_PRIVATE)
        }

        fun saveAlarmList(context: Context, alarmList: List<AlarmService.Alarm>) {
            val preferences = getAlarmPreferences(context)
            val editor = preferences.edit()
            val gson = Gson()
            val json = gson.toJson(alarmList)
            editor.putString(KEY_ALARM_LIST, json)
            editor.apply()

        }

        fun getAlarmList(context: Context): List<AlarmService.Alarm> {
            val preferences = getAlarmPreferences(context)
            val gson = Gson()
            val json = preferences.getString(KEY_ALARM_LIST, "")
            return gson.fromJson(json, object : TypeToken<List<AlarmService.Alarm>>() {}.type)
                ?: emptyList()
        }

        fun removeAlarm(context: Context, alarm: AlarmService.Alarm) {
            val alarmList = getAlarmList(context).toMutableList()
            alarmList.remove(alarm)
            saveAlarmList(context, alarmList)
        }

        fun removeAlarm(context: Context, id: String) {
            val alarmList = getAlarmList(context).toMutableList()
            val alarmToRemove = alarmList.find { it.taskId == id }
            alarmToRemove?.let {
                alarmList.remove(it)
                saveAlarmList(context, alarmList)
            }
        }

        fun clearAllData(context: Context) {
            val preferences = getAlarmPreferences(context)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
        }

        //存取基础类型
        fun <T> saveValue(context: Context, key: String, value: T) {
            val preferences = getAppSetPreferences(context)
            val editor = preferences.edit()

            when (value) {
                is String -> editor.putString(key, value)
                is Int -> editor.putInt(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Float -> editor.putFloat(key, value)

                else -> throw IllegalArgumentException("Unsupported type")
            }

            editor.apply()
        }

        fun <T> getValue(context: Context, key: String, defaultValue: T, clazz: Class<T>): T {
            val preferences = getAppSetPreferences(context)
            return when (clazz) {
                String::class.java -> preferences.getString(key, defaultValue as String) as T
                Int::class.java -> preferences.getInt(key, defaultValue as Int) as T
                Boolean::class.java -> preferences.getBoolean(key, defaultValue as Boolean) as T
                Float::class.java -> preferences.getFloat(key, defaultValue as Float) as T

                else -> throw IllegalArgumentException("Unsupported type")
            }
        }

        fun removeKey(context: Context, key: String) {
            val preferences = context.getSharedPreferences(APP_SET_NAME, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.remove(key)
            editor.apply()
        }

        fun <T> saveTypeValue(context: Context, key: String, value: T) {
            val preferences = getAppSetPreferences(context)
            val editor = preferences.edit()
            val gson = Gson()
            val json = gson.toJson(value)
            editor.putString(key, json)
            editor.apply()
        }

        fun <T> getTypeValue(context: Context, key: String, defaultValue: T, type: Type): T {
            val preferences = getAppSetPreferences(context)
            val gson = Gson()
            val json = preferences.getString(key, null)
            return if (json != null) {
                gson.fromJson(json, type)
            } else {
                defaultValue
            }
        }

        // 新增保存对象的方法
        fun <T> saveObject(context: Context, key: String, value: T) {
            val preferences = getAppSetPreferences(context)
            val editor = preferences.edit()
            val gson = Gson()
            val json = gson.toJson(value)
            editor.putString(key, json)
            editor.apply()
        }

        // 新增获取对象的方法
        fun <T> getObject(context: Context, key: String, clazz: Class<T>): T? {
            val preferences = getAppSetPreferences(context)
            val gson = Gson()
            val json = preferences.getString(key, null)
            return json?.let { gson.fromJson(it, clazz) }
        }


    }

}



