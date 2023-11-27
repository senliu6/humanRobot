package com.shciri.rosapp.server

/**
 * 功能：后台执行定时任务
 * @author ：liudz
 * 日期：2023年11月08日
 */
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.google.gson.reflect.TypeToken
import com.hjq.toast.Toaster
import com.shciri.rosapp.dmros.client.RosTopic
import com.shciri.rosapp.mydata.DBUtils
import com.shciri.rosapp.utils.SharedPreferencesUtil
import com.shciri.rosapp.utils.ToolsUtil
import src.com.jilk.ros.message.StateMachineRequest
import src.com.jilk.ros.message.requestparam.ManualPathParameter
import java.text.SimpleDateFormat
import java.util.*


class AlarmService : Service() {

    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private var alarmMap = mutableListOf<Alarm>()
    private val stateMachineRequest = StateMachineRequest()
    private val manualPathParameter = ManualPathParameter()


    data class Alarm(val taskId: String, val alarmTime: String)

    override fun onCreate() {
        super.onCreate()
        try {
            if (alarmMap.isEmpty()) {
                alarmMap = SharedPreferencesUtil.getAlarmList(applicationContext).toMutableList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            val taskId = it.getStringExtra("taskId") ?: ""
            val alarmTime = it.getStringExtra("alarmTime") ?: ""
            Toaster.showShort(taskId)
            if (taskId.isNotEmpty() && alarmTime.isNotEmpty()) {
                addAlarm(taskId, alarmTime)
            }
        }

        startAlarmClock()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarmClock()
    }

    fun addAlarm(taskId: String, alarmTime: String) {
        alarmMap.add(Alarm(taskId, alarmTime))
        SharedPreferencesUtil.saveAlarmList(applicationContext, alarmMap)
    }

    private fun startAlarmClock() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                checkAlarmTimes()
                handler.postDelayed(this, 1100) // 每隔一秒检查一次
            }
        }, 0)
    }

    private fun stopAlarmClock() {
        alarmMap.clear()
    }


    private fun checkAlarmTimes() {
        val currentTime = dateFormat.format(Date())
        try {
            alarmMap = SharedPreferencesUtil.getAlarmList(applicationContext).toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (alarmMap.isNotEmpty()) {
            for (alarm in alarmMap) {
                //任务触发
                if (currentTime == alarm.alarmTime) {
                    stateMachineRequest.navigation_task = 1
                    RosTopic.publishStateMachineRequest(stateMachineRequest)
                    manualPathParameter.point = DBUtils.getInstance().getPointS(alarm.taskId)
                    manualPathParameter.loop_num =
                        DBUtils.getInstance().getLoopNum(alarm.taskId).toShort()
                    RosTopic.publishManualPathParameterTopic(manualPathParameter)
                    stateMachineRequest.navigation_task = 3
                    RosTopic.publishStateMachineRequest(stateMachineRequest)
                    Toaster.showLong("触发定时任务${alarm.taskId}")
                    ToolsUtil.playRingtone(this)
                }
            }
        }

    }
}

