package com.shciri.rosapp.utils

import android.content.Context
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager


/**
 * 功能：
 * @author ：liudz
 * 日期：2023年11月08日
 */
object ToolsUtil {

    //播放响铃
    private var ringtone: Ringtone? = null
    fun playRingtone(context: Context?) {
        // 获取默认铃声的 Uri
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // 创建 Ringtone 对象
        ringtone = RingtoneManager.getRingtone(context, ringtoneUri)

        // 播放铃声
        if (ringtone != null) {
            ringtone!!.play()
        }
    }

    fun stopRingtone() {
        // 停止铃声
        if (ringtone != null && ringtone!!.isPlaying) {
            ringtone!!.stop()
        }
    }

    //DP转换
    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    fun dip2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    //颜色转换
    fun intToColorInt(context: Context, color: Int): Int {
        return Color.parseColor(context.getString(color))
    }
}