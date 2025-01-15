package com.shciri.rosapp.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.shciri.rosapp.rosdata.OccupancyGrid

fun occupancyGridToBitmap(occupancyGrid: OccupancyGrid): Bitmap? {
    val width = occupancyGrid.info.width.toInt()
    val height = occupancyGrid.info.height.toInt()
    val data = occupancyGrid.data

    // 创建空白 Bitmap
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // 将栅格数据转换为灰度图
    for (y in 0 until height) {
        for (x in 0 until width) {
            val value = data[x + y * width]
            val color = when {
                value.toInt() == -1 -> Color.GRAY // 未知区域
                value in 0..50 -> Color.WHITE // 空闲区域
                else -> Color.BLACK // 占用区域
            }
            bitmap.setPixel(x, height - y - 1, color) // 注意坐标系的垂直翻转
        }
    }

    return bitmap
}
