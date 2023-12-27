package com.shciri.rosapp.ui.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.shciri.rosapp.R;

public class TaskBtView extends ConstraintLayout{

    public TextView titleTv;
    TextView currFunStatusTv;
    Spinner mSpinner;
    View view;
    View taskBtView;
    public View detailPage;
    ImageView mEditIv;
    ImageView pullIv;
    private String[] starArray;
    ArrayAdapter<String> starAdapter;

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
        titleTv = (TextView) findViewById(R.id.task_title);
        mEditIv = (ImageView) findViewById(R.id.editIv);
        mSpinner = findViewById(R.id.task_bt_spinner);
        detailPage = findViewById(R.id.detail_page_Tv);
        taskBtView = findViewById(R.id.task_bt_run_status_view);
        currFunStatusTv = findViewById(R.id.task_bt_status_tv);
        pullIv = findViewById(R.id.task_bt_pull_iv);

        initSpinner(context);

        taskBtView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinner.performClick();
            }
        });

//        view.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        mSpinner.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    requestFocus();
                }
                return false;
            }
        });

        view.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleAnimation animation = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    //持续时间
                    animation.setDuration(200);
                    //动画结束是否保持结束状态
                    animation.setFillAfter(true);
                    view.startAnimation(animation);
                    detailPage.setVisibility(VISIBLE);
                    mEditIv.setVisibility(VISIBLE);
                    pullIv.setVisibility(VISIBLE);
                    view.performClick();
                } else {
                    view.clearAnimation();
                    detailPage.setVisibility(INVISIBLE);
                    mEditIv.setVisibility(INVISIBLE);
                    pullIv.setVisibility(INVISIBLE);
                }
            }

        });
    }

    private void initSpinner(Context context) {
        starArray = new String[]{context.getString(R.string.empty_run),context.getString(R.string.disinfection)
                , context.getString(R.string.air), context.getString(R.string.empty_run)};
        starAdapter = new ArrayAdapter<String>(context, R.layout.task_bt_spinner_item_select, starArray);
        //设置数组适配器的布局样式
        starAdapter.setDropDownViewResource(R.layout.task_bt_spinner_item_drapdown);
        //设置下拉框的数组适配器
        mSpinner.setAdapter(starAdapter);
        //给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        mSpinner.setOnItemSelectedListener(new MyTaskBtSpinner());
    }

    class MyTaskBtSpinner implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            currFunStatusTv.setText(starAdapter.getItem(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void setTitleTv(String name) {
        titleTv.setText(name);
    }

    public String getCurrentMode() {
        return currFunStatusTv.getText().toString();
    }
}
