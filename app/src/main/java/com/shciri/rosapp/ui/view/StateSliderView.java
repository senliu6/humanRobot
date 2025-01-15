package com.shciri.rosapp.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hjq.shape.layout.ShapeLinearLayout;
import com.hjq.shape.view.ShapeButton;
import com.hjq.shape.view.ShapeTextView;
import com.hjq.shape.view.ShapeView;
import com.shciri.rosapp.R;


public class StateSliderView extends FrameLayout {
    private ShapeButton slider;
    private ShapeTextView stateIdle;
    private ShapeTextView stateNavigating;
    private ShapeTextView stateCharging;
    private static final int STATE_IDLE = 0;
    private static final int STATE_NAVIGATING = 1;
    private static final int STATE_CHARGING = 2;
    private int currentState = STATE_IDLE;

    public StateSliderView(Context context) {
        super(context);
        init(context);
    }

    public StateSliderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StateSliderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 创建一个 LinearLayout 作为状态容器
        ShapeLinearLayout stateContainer = new ShapeLinearLayout(context);
        stateContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        stateContainer.setOrientation(LinearLayout.HORIZONTAL);
        stateContainer.setGravity(Gravity.CENTER);
        stateContainer.getShapeDrawableBuilder().setRadius(30.0f);
        stateContainer.getShapeDrawableBuilder().setStrokeColor(R.color.black);
        stateContainer.getShapeDrawableBuilder().setStrokeSize(2);
        stateContainer.getShapeDrawableBuilder().intoBackground();

        // 添加状态文本视图
        stateIdle = new ShapeTextView(context);
        stateIdle.setText("空闲");
        stateIdle.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
        stateIdle.setGravity(Gravity.CENTER);
        stateIdle.setTextColor(Color.BLACK);
        stateIdle.setTextSize(20f);
        stateContainer.addView(stateIdle);

        stateNavigating = new ShapeTextView(context);
        stateNavigating.setText("导航");
        stateNavigating.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
        stateNavigating.setGravity(Gravity.CENTER);
        stateNavigating.setTextColor(Color.BLACK);
        stateIdle.setTextSize(20f);
        stateContainer.addView(stateNavigating);

        stateCharging = new ShapeTextView(context);
        stateCharging.setText("充电");
        stateCharging.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
        stateCharging.setGravity(Gravity.CENTER);
        stateCharging.setTextColor(Color.BLACK);
        stateIdle.setTextSize(20f);
        stateContainer.addView(stateCharging);

        // 创建滑块
        slider = new ShapeButton(context);
        slider.setLayoutParams(new LayoutParams(50, 50, 1));
        slider.getShapeDrawableBuilder().setSolidColor(R.color.blue_bg);
        slider.getShapeDrawableBuilder().intoBackground();
        

        // 将滑块添加到 FrameLayout 中
        addView(slider);
        addView(stateContainer);

    }

    public void setState(int state) {
        if (state < STATE_IDLE || state > STATE_CHARGING) {
            throw new IllegalArgumentException("Invalid state value");
        }
        currentState = state;
        updateSliderPosition();
    }

    private void updateSliderPosition() {
        int containerWidth = getWidth();
        int targetMarginLeft = containerWidth / 3 * currentState;
        FrameLayout.LayoutParams params = (LayoutParams) slider.getLayoutParams();
        params.leftMargin = targetMarginLeft;
        slider.setLayoutParams(params);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateSliderPosition();
    }
}