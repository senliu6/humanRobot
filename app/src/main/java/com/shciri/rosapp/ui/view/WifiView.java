package com.shciri.rosapp.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * 功能：绘画WIFI
 * 作者：ZhengYuanZhang
 * 日期：2023年07月19日
 */
public class WifiView extends View {

    //绘画笔
    private Paint paint = new Paint();


    public WifiView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);

        int width = getWidth();
        int height = getHeight();

       /* // 绘制外圆
        canvas.drawCircle(width/2, height/2, width/4, paint);

        // 绘制中间的三条线
        canvas.drawLine(width/2 - width/10, height/2 - height/20, width/2 - width/10, height/2 + height/20, paint);
        canvas.drawLine(width/2, height/2 + height/10, width/2, height/2 + height/4, paint);
        canvas.drawLine(width/2 + width/10, height/2 - height/20, width/2 + width/10, height/2 + height/20, paint);

        // 绘制内部的圆和连接线
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(width/2, height/2, width/10, paint);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        canvas.drawLine(width/2 - width/10, height/2, width/2 - width/5, height/2, paint);
        canvas.drawLine(width/2 + width/10, height/2, width/2 + width/5, height/2, paint);*/
    }



}
