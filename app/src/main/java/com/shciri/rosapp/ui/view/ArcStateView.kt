package com.shciri.rosapp.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.shciri.rosapp.R

class ArcStateView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val rectF: RectF = RectF()
    private val indicatorRect: RectF = RectF()

    private var currentState = 0 // 默认状态是 "空闲" (0 -> IDLE)
    private var indicatorPosition = 0f // 滑块的位置
    private val stateLabels = arrayOf(context.getString(R.string.idle), context.getString(R.string.navigation), context.getString(R.string.charging)) // 三个状态的标签

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL

        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 30f
    }

    // 绘制外部圆弧背景和滑块
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // 绘制外部圆弧背景
        paint.color = Color.LTGRAY
        rectF.set(0f, 0f, width, height)
        canvas.drawRoundRect(rectF, height / 2, height / 2, paint)

        // 绘制选中的状态背景
        paint.color = when (currentState) {
            0 -> Color.GREEN  // 空闲状态
            1 -> Color.YELLOW // 导航状态
            2 -> Color.RED    // 充电状态
            else -> Color.GRAY // 默认灰色
        }
        canvas.drawRoundRect(rectF, height / 2, height / 2, paint)

        // 绘制滑块
        val sliderWidth = width / 3f
        indicatorPosition = when (currentState) {
            0 -> 0f
            1 -> width / 2f - sliderWidth / 2f
            2 -> width - sliderWidth
            else -> 0f // 默认位置
        }
        paint.color = Color.WHITE
        indicatorRect.set(indicatorPosition, 0f, indicatorPosition + sliderWidth, height)
        canvas.drawRoundRect(indicatorRect, height / 2, height / 2, paint)

        // 绘制状态标签
        textPaint.color = Color.BLACK
        canvas.drawText(stateLabels[0], width / 6f, height / 2f + 10, textPaint)
        canvas.drawText(stateLabels[1], width / 2f, height / 2f + 10, textPaint)
        canvas.drawText(stateLabels[2], width * 5f / 6f, height / 2f + 10, textPaint)
    }

    // 处理触摸事件
    override fun onTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
//                val width = width.toFloat()
//                indicatorPosition = event.x.coerceIn(0f, width)
//
//                // 根据滑动的位置更新状态
//                currentState = when {
//                    indicatorPosition < width / 3 -> 0 // 空闲
//                    indicatorPosition < 2 * width / 3 -> 1 // 导航
//                    else -> 2 // 充电
//                }
//
//                invalidate() // 重新绘制
//            }
//            MotionEvent.ACTION_UP -> {
//                // 在滑动结束时，确定最终状态
//                val width = width.toFloat()
//                currentState = when {
//                    indicatorPosition < width / 3 -> 0 // 空闲
//                    indicatorPosition < 2 * width / 3 -> 1 // 导航
//                    else -> 2 // 充电
//                }
//                invalidate() // 重新绘制
//            }
//        }
        return true
    }

    // 外部设置状态方法
    fun setCurrentState(state: Int) {
        if (currentState != state) {
            currentState = state
            indicatorPosition = when (state) {
                0 -> 0f
                1 -> width / 2f - (width / 3f) / 2f
                2 -> width - width / 3f
                else -> 0f // 默认位置
            }
            invalidate() // 重新绘制
        }
    }
}
