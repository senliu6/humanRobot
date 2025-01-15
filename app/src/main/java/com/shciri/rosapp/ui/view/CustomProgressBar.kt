package com.shciri.rosapp.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.shciri.rosapp.R

/**
 * 功能：圆角矩形进度条
 * @author ：liudz
 * 日期：2024年03月14日
 */
class CustomProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress: Int = 0
    private var maxProgress: Int = 100
    private var progressBarColor: Int = Color.BLUE
    private var circleColor: Int = Color.RED
    private var progressTextColor: Int = Color.WHITE
    private var progressTextSize: Float = 48f

    // 在CustomProgressBar中添加
    private var orientation: Int = 0 // 默认为水平

    private val progressBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val progressTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomProgressBar,
            0, 0
        ).apply {
            try {
                progress = getInteger(R.styleable.CustomProgressBar_progress, progress)
                maxProgress = getInteger(R.styleable.CustomProgressBar_maxProgress, maxProgress)
                progressBarColor =
                    getColor(R.styleable.CustomProgressBar_progressBarColor, progressBarColor)
                circleColor = getColor(R.styleable.CustomProgressBar_circleColor, circleColor)
                progressTextColor =
                    getColor(R.styleable.CustomProgressBar_progressTextColor, progressTextColor)
                progressTextSize =
                    getDimension(R.styleable.CustomProgressBar_progressTextSize, progressTextSize)
                orientation = getInt(R.styleable.CustomProgressBar_orientation, orientation)
            } finally {
                recycle()
            }
        }

        progressBarPaint.color = progressBarColor
        circlePaint.color = circleColor
        progressTextPaint.apply {
            color = progressTextColor
            textSize = progressTextSize
            textAlign = Paint.Align.CENTER
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return



        if (orientation == 0) {
            // Draw the background progress bar
            val cornerRadius = 30f
            val backgroundRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            progressBarPaint.color = progressBarColor
            canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, progressBarPaint)

            // Calculate progress width
            val progressWidth = width * progress / maxProgress
            val progressRect = RectF(0f, 0f, progressWidth.toFloat(), height.toFloat())
            progressBarPaint.color = circleColor
            canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressBarPaint)

            // Draw the progress text
            val progressText = "$progress%"
            val textX = width / 2f
            val textY =
                (height / 2f) - ((progressTextPaint.descent() + progressTextPaint.ascent()) / 2f)
            canvas.drawText(progressText, textX, textY, progressTextPaint)
        } else {
            // 竖直绘制逻辑
            val cornerRadius = 30f
            // 画背景
            val backgroundRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            progressBarPaint.color = progressBarColor
            canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, progressBarPaint)
            // 计算进度的高度
            val progressHeight = height * progress / maxProgress
            val progressRect = RectF(
                0f,
                (height - progressHeight).toFloat(), width.toFloat(), height.toFloat()
            )
            progressBarPaint.color = circleColor
            canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressBarPaint)
            // 绘制文本
            val progressText = "$progress%"
            val textX = width / 2f
            val textY =
                height / 2f - ((progressTextPaint.descent() + progressTextPaint.ascent()) / 2f)
            canvas.drawText(progressText, textX, textY, progressTextPaint)
        }
    }

    fun setProgress(newProgress: Int) {
        progress = newProgress.coerceIn(0, maxProgress)
        invalidate() // Redraw the view
    }

    fun setProgressColor(color: Int) {
        progressBarColor = color
        progressBarPaint.color = progressBarColor
        invalidate() // Redraw the view
    }

    // Similar setters for other properties
}
