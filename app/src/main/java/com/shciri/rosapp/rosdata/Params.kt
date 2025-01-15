package com.shciri.rosapp.rosdata

/**
 * 功能：
 * @author ：liudz
 * 日期：2024年03月15日
 */
class Params {
}

//手臂控制参数
data class PalmControl(
    val nums: IntArray,//(100,200,300,1000,2000,1000)
    var value: String //  left/right
)

data class RowData(
    val angleArray: Array<Int>,    // 前6个EditText数值
    val speedArray: Array<Int>,    // 中间6个EditText数值
    val strengthArray: Array<Int>,  // 后6个EditText数值
    var t_mode: Byte = 0, // 0:定位模式 1:伺服模式 2:速度模式 3:力控模式
    val interval: Int //时间间隔
)

data class ControlDataS(
    val dataList: List<RowData>,
    val name: String
)
