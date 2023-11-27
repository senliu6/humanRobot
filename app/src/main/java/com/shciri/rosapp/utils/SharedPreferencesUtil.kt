package com.shciri.rosapp.utils

/**
 * 功能：
 * @author ：liudz
 * 日期：2023年11月08日
 */
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shciri.rosapp.server.AlarmService

class SharedPreferencesUtil {

    companion object {
        private const val PREFERENCES_NAME = "ALARM_LIST"
        private const val KEY_ALARM_LIST = "key_alarm_list"

        fun saveAlarmList(context: Context, alarmList: List<AlarmService.Alarm>) {
            val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            val gson = Gson()
            val json = gson.toJson(alarmList)
            editor.putString(KEY_ALARM_LIST, json)
            editor.apply()
        }

        fun getAlarmList(context: Context): List<AlarmService.Alarm> {
            val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
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
            val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
        }
    }
}
