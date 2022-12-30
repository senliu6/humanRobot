package com.shciri.rosapp.ui.manualcontrol;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shciri.rosapp.R;


public class OperatingArmView extends RelativeLayout implements View.OnTouchListener {

    TextView fingerView;
    TextView backView;
    float centerX, centerY, fingerX, fingerY;
    float lastX, lastY;//小圆自动回归中点动画中上一点的位置
    float backRadius, innerRadius;
    float radiusBorder = (backRadius - innerRadius);//大圆减去小圆的半径
    private ValueAnimator positionAnimator;//自动回中的动画
    private budgeListener budgeListener;//移动回调的接口

    public OperatingArmView(Context context) {
        super(context);
        initView(context);
    }

    public OperatingArmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public OperatingArmView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        locationCoordinates();
    }

    private void locationCoordinates(){
        backRadius = backView.getWidth()/2f;

        innerRadius = fingerView.getHeight()/2f;
        radiusBorder = backRadius - innerRadius;

        //中心点坐标
        centerX = backRadius;
        centerY = backRadius;
    }

    //初始化UI，可根据业务需求设置默认值。
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.activity_operating_arm_view, this, true);
        backView = (TextView) findViewById(R.id.back_view);
        fingerView = (TextView) findViewById(R.id.finger_view);

        setOnTouchListener(this);
        positionAnimator = ValueAnimator.ofFloat(1);
        positionAnimator.addUpdateListener(animation -> {
            Float aFloat = (Float) animation.getAnimatedValue();
            changeFingerPosition(lastX + (centerX - lastX) * aFloat, lastY + (centerY - lastY) * aFloat);
        });
    }

    private void changeFingerPosition(float fingerX, float fingerY) {
        this.fingerX = fingerX;
        this.fingerY = fingerY;

        lastX = fingerX;
        lastY = fingerY;

        fingerView.setX(fingerX - innerRadius);
        fingerView.setY(fingerY - innerRadius);
        if (budgeListener != null) {
            budgeListener.budge((fingerX - centerX) / radiusBorder, (centerY - fingerY) / radiusBorder);
        }
    }

    @Override protected void finalize() throws Throwable {
        super.finalize();
        positionAnimator.removeAllListeners();
    }

    public void setBudgeListener(budgeListener budgeListener) {
        this.budgeListener = budgeListener;
    }

    /**
     *回调事件的接口
     **/
    public interface budgeListener {
        void budge(float dx, float dy);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.v("control", "onInterceptTouchEvent");
        return true;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float evx = motionEvent.getX(), evy = motionEvent.getY();
        float deltaX = evx - centerX, deltaY = evy - centerY;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.v("control", "ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                //如果触摸点在圆外
                if (Math.abs(deltaX) > radiusBorder || Math.abs(deltaY) > radiusBorder) {
                    float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                    changeFingerPosition(centerX + (deltaX * radiusBorder / distance),
                            centerY + (deltaY * radiusBorder / distance));
                } else { //如果触摸点在圆内
                    changeFingerPosition(evx, evy);
                }
                positionAnimator.cancel();
                break;
            case MotionEvent.ACTION_UP:
                //Log.v("control", "ACTION_UP");
                positionAnimator.setDuration(500);
                positionAnimator.start();
                break;
        }
        return true;
    }
}