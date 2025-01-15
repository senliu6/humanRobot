package com.shciri.rosapp.ui.view

/**
 * 功能：
 * @author ：liudz
 * 日期：2024年03月14日
 */
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class VerticalSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnSeekBarChangeListener {
        fun onProgressChanged(seekBar: VerticalSeekBar, progress: Int, fromUser: Boolean)
//        fun onStartTrackingTouch(seekBar: VerticalSeekBar)
        fun onStopTrackingTouch(seekBar: VerticalSeekBar)
    }

    private var listener: OnSeekBarChangeListener? = null

    var progress = 0
    private var maxProgress = 100
    private var barWidth = 40f // 进度条宽度
    private var thumbRadius = 30f // 滑块半径
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt() // 数值颜色，默认白色
        textSize = 15f // 字体大小
        textAlign = Paint.Align.CENTER
    }
    private var progressColor = 0xFF0000FF.toInt() // 进度颜色，默认蓝色
    private var thumbColor = 0xFFFF0000.toInt() // 滑块颜色，默认红色
    private var backgroundColor = 0xFFCCCCCC.toInt() // 背景色，默认灰色

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        barWidth = w * 2 / 3f
        thumbRadius = w / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制背景
        paint.color = backgroundColor
        val backgroundRect = RectF(
            width / 2f - barWidth / 2, thumbRadius,
            width / 2f + barWidth / 2, height.toFloat() - thumbRadius
        )
        canvas.drawRoundRect(backgroundRect, barWidth / 2, barWidth / 2, paint)

        // 绘制进度
        paint.color = progressColor
        val progressHeight = (height - 2 * thumbRadius) * progress / maxProgress
        val progressRect = RectF(
            width / 2f - barWidth / 2, height - thumbRadius - progressHeight,
            width / 2f + barWidth / 2, height.toFloat() - thumbRadius
        )
        canvas.drawRoundRect(progressRect, barWidth / 2, barWidth / 2, paint)

        // 绘制滑块
        paint.color = thumbColor
        val thumbY = height - thumbRadius - progressHeight
        canvas.drawCircle(width / 2f, thumbY, thumbRadius, paint)

        // 在滑块上显示进度数值
//        val progressText = "${(progress.toFloat() / maxProgress * 100).toInt()}%"
        val progressText = "${progress}"
        canvas.drawText(progressText, width / 2f, thumbY - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
//                listener?.onStartTrackingTouch(this)
                updateProgress(event)
            }
            MotionEvent.ACTION_MOVE -> {
                updateProgress(event)
                listener?.onProgressChanged(this, progress, true)
            }
            MotionEvent.ACTION_UP -> {
                listener?.onStopTrackingTouch(this)
            }
        }
        return true
    }

    private fun updateProgress(event: MotionEvent) {
        var newY = event.y.coerceIn(thumbRadius, height.toFloat() - thumbRadius)
        newY = height.toFloat() - newY // 调整Y方向，以适应从下向上的进度变化
        progress = ((newY - thumbRadius) / (height - 2 * thumbRadius) * maxProgress).toInt().coerceIn(0, maxProgress)
        invalidate()
    }


    override fun performClick(): Boolean {
        // Call the superclass implementation
        return super.performClick()
    }

    fun setNowProgress(progress: Int) {
        this.progress = progress.coerceIn(0, maxProgress)
        invalidate()
    }

    fun getNowProgress(): Int = progress

    fun setMax(maxProgress: Int) {
        this.maxProgress = maxProgress
        invalidate()
    }

    fun getMax(): Int = maxProgress

    fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener) {
        this.listener = listener
    }
}
