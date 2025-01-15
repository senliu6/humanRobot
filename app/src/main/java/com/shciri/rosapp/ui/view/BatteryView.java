package com.shciri.rosapp.ui.view;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class BatteryView extends View {

    private float percent = 0f;
    // 电池电量外面的大白框
    Paint framePaint = new Paint();
    // 电池电量里面的白色
    Paint contentPaint = new Paint();
    // 电池头部
    Paint headPaint = new Paint();

    public BatteryView(Context context, AttributeSet set) {
        super(context, set);
        // 去锯齿
        contentPaint.setAntiAlias(true);
        contentPaint.setStyle(Paint.Style.FILL);
        contentPaint.setColor(Color.WHITE);

        framePaint.setAntiAlias(true);
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(2);
        framePaint.setColor(Color.GRAY);

        headPaint.setAntiAlias(true);
        headPaint.setStyle(Paint.Style.FILL);
        headPaint.setColor(Color.GRAY);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mScreenWidth = dm.widthPixels;
        int mScreenHeight = dm.heightPixels;

        //以分辨率为1920*1200准，计算宽高比值
        float ratioWidth = (float) mScreenWidth / 1920;
        float ratioHeight = (float) mScreenHeight / 1200;
        float ratioMetrics = Math.min(ratioWidth, ratioHeight);
        int textSize = Math.round(20 * ratioMetrics);
        headPaint.setTextSize(textSize);
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @SuppressLint("DrawAllocation")
    @Override
    // 重写该方法,进行绘图
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 大于百分之30时绿色，否则为红色
        if (percent > 0.3f) {
            contentPaint.setColor(Color.WHITE);
        } else {
            contentPaint.setColor(Color.RED);
        }

        int a = getWidth() - dip2px(2f);
        int b = getHeight() - dip2px(2f);
        // 根据电量百分比画图
        float d = a * percent;
        float left = dip2px(0.5f);
        float top = dip2px(0.5f);
        float right = dip2px(0.5f);
        float bottom = dip2px(0.5f);

        RectF re1 = new RectF(left + 2, top + 2, d - 3 - right , b + bottom - 3); //电量填充
        RectF re2 = new RectF(0, 0, a - right, b + bottom); //电池边框
        RectF re3 = new RectF(a - right + 3, b / 4f, a + 8, b + bottom - b / 4f);  //电池正极

        // 绘制圆角矩形
        canvas.drawRoundRect(re1, 3, 3, contentPaint);
        canvas.drawRoundRect(re2, 5, 5, framePaint);
        canvas.drawRoundRect(re3, 5, 5, headPaint);
        //文字的起点为(getWidth()/2,getHeight()/2)
        //canvas.drawText(String.valueOf((int) (percent * 100)), getWidth() / 3 - dip2px(3), getHeight() - getHeight() / 4, headPaint);
    }

    // 每次检测电量都重绘，在检测电量的地方调用
    public synchronized void setProgress(int percent) {
        this.percent = (float) (percent / 100.0);
        postInvalidate();
    }
}
