package com.shciri.rosapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.util.Base64
import java.io.ByteArrayOutputStream


/**
 * 功能：工具类
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

    // 将 Bitmap 转换为 Base64 字符串
    // 将 Bitmap 转换为 Base64 字符串
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // 将 Base64 字符串转换为 Bitmap
    fun base64ToBitmap(base64Str: String): Bitmap? {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun saveBitmapList(context: Context, key: String, bitmapList: List<Bitmap>) {
        val base64List = bitmapList.map { bitmapToBase64(it) } // 转换为 Base64 字符串列表
        val sharedPreferences = context.getSharedPreferences("app_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet(key, base64List.toSet()) // 保存为字符串集合
        editor.apply()
    }

    fun loadBitmapList(context: Context, key: String): List<Bitmap> {
        val sharedPreferences = context.getSharedPreferences("app_data", Context.MODE_PRIVATE)
        val base64Set = sharedPreferences.getStringSet(key, emptySet()) ?: emptySet()
        return base64Set.mapNotNull { base64ToBitmap(it) } // 转换回 Bitmap 列表
    }


}