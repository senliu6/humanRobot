package com.shciri.rosapp.ui.view

/**
 * 功能：
 * @author ：liudz
 * 日期：2024年01月06日
 */
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.shciri.rosapp.R
import com.shciri.rosapp.RCApplication
import com.shciri.rosapp.utils.protocol.RequestIPC
import kotlin.math.min

class CircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0
    private var maxProgress = 100
    private var startAngle = -90f
    private var strokeWidth = 40f
    private var progressColor = Color.BLUE
    private var backgroundColor = Color.GRAY

    private val rectF = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val dotRadius = 20f // 小圆圈的半径

    var shouldShowText = true  // 设置为 true 或 false 来控制显示

    var isSlide = false



    init {
        // 获取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar)
        progressColor =
            typedArray.getColor(R.styleable.CircularProgressBar_progressColor, Color.BLUE)
        backgroundColor =
            typedArray.getColor(R.styleable.CircularProgressBar_backgroundColor, Color.GRAY)
        strokeWidth = typedArray.getDimension(R.styleable.CircularProgressBar_strokeWidth, 20f)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY) - strokeWidth / 2 - dotRadius

        // 画小圆圈
        val dotAngle = startAngle + 360f * (progress.toFloat() / maxProgress)
        val dotX = centerX + radius * Math.cos(Math.toRadians(dotAngle.toDouble())).toFloat()
        val dotY = centerY + radius * Math.sin(Math.toRadians(dotAngle.toDouble())).toFloat()


        // 画背景
        paint.color = backgroundColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        canvas.drawCircle(centerX, centerY, radius, paint)

        // 画进度
        paint.color = progressColor
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        canvas.drawArc(rectF, startAngle, 360f * (progress.toFloat() / maxProgress), false, paint)

        // 画百分比文字
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        var text = ""
        text = if (shouldShowText){
            "${(progress.toFloat() / maxProgress * 100).toInt()}%"
        }else{
            "${(progress.toFloat() * 35)}"
        }
        paint.textSize = strokeWidth * 4
        val textWidth = paint.measureText(text)
        canvas.drawText(text, centerX - textWidth / 2, centerY + strokeWidth, paint)

        // 画小圆圈
        paint.color = progressColor
        paint.style = Paint.Style.FILL
        canvas.drawCircle(dotX, dotY, dotRadius, paint)
//        canvas.drawBitmap(dotBitmap, dotX - dotBitmap.width / 2, dotY - dotBitmap.height / 2, paint)

    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isSlide){
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    updateProgress(event.x, event.y)
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    // 监听是何时结束滑动
                    val data: ByteArray
                    //最后结束时候的的值传给设备
                    //最后结束时候的的值传给设备
                    data = RequestIPC.fanControlRequest(progress.toByte())
                    RCApplication.uartVCP.sendData(data)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun updateProgress(x: Float, y: Float) {
        val centerX = width / 2f
        val centerY = height / 2f
        val angle =
            Math.toDegrees(Math.atan2((y - centerY).toDouble(), (x - centerX).toDouble())).toFloat()
        val normalizedAngle = if (angle < 0) 360 + angle else angle
        progress = ((normalizedAngle / 360) * maxProgress).toInt()
        invalidate()
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun setTextShow(show: Boolean) {
        this.shouldShowText = show
        invalidate()
    }
    fun setSlider(slider: Boolean) {
        this.isSlide = slider
        invalidate()
    }

    fun getMaxProgress(): Int {
        return maxProgress
    }

    fun setMaxProgress(maxProgress: Int) {
        this.maxProgress = maxProgress
        invalidate()
    }

    fun setProgressColor(color: Int) {
        progressColor = color
        invalidate()
    }

    override fun setBackgroundColor(color: Int) {
        backgroundColor = color
        invalidate()
    }
}
