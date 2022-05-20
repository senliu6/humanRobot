package com.shciri.rosapp.ui.myview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shciri.rosapp.R;

public class TaskBtView extends RelativeLayout {

    TextView mTitleTv;
    Spinner mSpinner;
    View view;

    public TaskBtView(Context context) {
        super(context);
        initView(context);
    }

    public TaskBtView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TaskBtView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        view = LayoutInflater.from(context).inflate(R.layout.view_task_bt, this, true);
        mTitleTv = (TextView) findViewById(R.id.task_bt_title);
        mSpinner = (Spinner)findViewById(R.id.task_bt_spinner);

        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //第一次是取得焦点，第二次才会执行onClick，点击两次显然不太好,所以做一下处理
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();
                }
                return false;
            }
        });

        view.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                System.out.println("xxxxx");
                if(hasFocus) {
                    ScaleAnimation animation =new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    //持续时间
                    animation.setDuration(200);
                    //动画结束是否保持结束状态
                    animation.setFillAfter(true);
                    view.startAnimation(animation);
                }else{
                    view.clearAnimation();
                }
            }
        });
    }

    public void setTitle(String title){
        mTitleTv.setText(title);
    }
}
