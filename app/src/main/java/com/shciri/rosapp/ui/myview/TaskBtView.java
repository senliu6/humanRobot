package com.shciri.rosapp.ui.myview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
//<<<<<<< HEAD
//import android.view.MotionEvent;
//=======
//>>>>>>> eda724c2ecef5f508a13093319d17919f8d26a31
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.shciri.rosapp.R;

import java.util.ArrayList;

public class TaskBtView extends ConstraintLayout{

    TextView mTitleTv;
    Spinner mSpinner;

    View view;

//    View mMoreInfo;
//    ImageView mEditIv;
//    ArrayList<MoreInfoClickListener> mClickListeners = new ArrayList<>();


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

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.view_task_bt, this, true);
        mTitleTv = (TextView) findViewById(R.id.task_title);
        mSpinner = (Spinner) findViewById(R.id.spinner);

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
                if (hasFocus) {
                    ScaleAnimation animation = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    //持续时间
                    animation.setDuration(200);
                    //动画结束是否保持结束状态
                    animation.setFillAfter(true);
                    view.startAnimation(animation);
                } else {
                    view.clearAnimation();
                }
            }
        });
    }
//=======
//    private void initView(Context context) {
//        LayoutInflater.from(context).inflate(R.layout.view_task_bt, this, true);
//        mTitleTv = (TextView) findViewById(R.id.task_title);
//        mEditIv = (ImageView) findViewById(R.id.editIv);
//        mSpinner = (Spinner) findViewById(R.id.spinner);
//        mMoreInfo = findViewById(R.id.moreInfo);
//        mMoreInfo.setOnClickListener(this);
//        mEditIv.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "click Edit ImageView!!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public void setMoreInfoVisibility(int isVisibility) {
//        mMoreInfo.setVisibility(isVisibility);
//    }
//
//    public void setTitle(String title) {
//        mTitleTv.setText(title);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.moreInfo:
//                for (MoreInfoClickListener listener : mClickListeners) {
//                    listener.onClick();
//                }
//                break;
//        }
//>>>>>>> eda724c2ecef5f508a13093319d17919f8d26a31
//    }

//    public void setMoreInfoClickListener(MoreInfoClickListener listener){
//        mClickListeners.add(listener);
//    }

//    public interface MoreInfoClickListener {
//        void onClick();
//    }
}
