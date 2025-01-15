package com.shciri.rosapp.server

/**
 * 功能：后台执行定时任务
 * @author ：liudz
 * 日期：2023年11月08日
 */
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.hjq.toast.Toaster
import com.shciri.rosapp.dmros.client.RosTopic
import com.shciri.rosapp.rosdata.DBUtils
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
    private var serviceStatus = false


    data class Alarm(
        val taskId: String,
        val alarmTime: String,
        val week: String,
        val loopNum: Short,
    )

    override fun onCreate() {
        super.onCreate()
        try {
            if (alarmMap.isEmpty()) {
                alarmMap = SharedPreferencesUtil.getAlarmList(applicationContext).toMutableList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        startAlarmClock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { it ->
            val taskId = it.getStringExtra("taskId") ?: ""
            val alarmTime = it.getStringExtra("alarmTime") ?: ""
            val week = it.getStringExtra("week") ?: ""
            val loopNum = it.getShortExtra("loopNum", 0.toShort())

            if (taskId.isNotEmpty() && alarmTime.isNotEmpty()) {
                addAlarm(taskId, alarmTime, week, loopNum)

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

    fun addAlarm(
        taskId: String,
        alarmTime: String,
        week: String,
        loopNum: Short
    ) {
        alarmMap.add(Alarm(taskId, alarmTime, week, loopNum))
        SharedPreferencesUtil.saveAlarmList(applicationContext, alarmMap)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun startAlarmClock() {
        if (!serviceStatus){
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    checkAlarmTimes()
                    handler.postDelayed(this, 1000) // 每隔一秒检查一次
                }
            }, 1000)
        }
        serviceStatus = true

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
//                Log.d(
//                    "CeshiTAG",
//                    "任务周" + alarm.week + "转换后" + stringToIntList(alarm.week).size + "---" + getCurrentDayOfWeek()
//                );
                if (stringToIntList(alarm.week).containsCurrentDay()) {
                    if (currentTime == alarm.alarmTime) {
                        stateMachineRequest.navigation_task = 1
                        RosTopic.publishStateMachineRequest(stateMachineRequest)
                        manualPathParameter.point = DBUtils.getInstance().getPointS(alarm.taskId,false)
                        manualPathParameter.loop_num = alarm.loopNum
                        RosTopic.publishManualPathParameterTopic(manualPathParameter)
                        stateMachineRequest.navigation_task = 3
                        Toaster.showLong("触发定时任务${alarm.taskId}")
                        RosTopic.publishStateMachineRequest(stateMachineRequest)
                        ToolsUtil.playRingtone(this)
                    }
                }
            }
        }
    }

    private fun stringToIntList(input: String): List<Int> {
        return input.split(",").map { it.toInt() }
    }


    private fun List<Int>.containsCurrentDay(): Boolean {
        val currentDay = getCurrentDayOfWeek()
        return contains(currentDay)
    }

    private fun getCurrentDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_WEEK)
    }
}

