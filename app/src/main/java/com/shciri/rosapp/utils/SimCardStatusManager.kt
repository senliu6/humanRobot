package com.shciri.rosapp.utils

/**
 * 功能：Sim卡检测工具
 * @author ：liudz
 * 日期：2024年01月05日
 */
import android.content.Context
import android.telephony.TelephonyManager
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.widget.Toast

class SimCardStatusManager(private val context: Context) {

    var simStatusOpen:Boolean = false

    fun checkSimCardStatus() {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (telephonyManager.simState == TelephonyManager.SIM_STATE_UNKNOWN) {
            simStatusOpen = false
            // 无 SIM 卡
        } else {
            simStatusOpen = true
            // 有 SIM 卡，显示信号等级
            showToast("SIM 卡状态: ${getSignalStrength()}")
        }

    }

    private fun getSignalStrength(): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val phoneStateListener = object : PhoneStateListener() {
            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                // 获取信号强度
                val level = when {
                    signalStrength.isGsm && signalStrength.gsmSignalStrength != 99 -> signalStrength.gsmSignalStrength
                    signalStrength.cdmaDbm != -1 -> signalStrength.cdmaDbm
                    else -> 0
                }

                showToast("信号强度: $level")
            }
        }

        // 注册监听器
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)

        // 取消监听器注册，避免内存泄漏
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)

        return "获取中..." // 由于是异步操作，这里返回一个提示信息
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
