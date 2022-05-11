package com.shciri.rosapp.ui.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.shciri.rosapp.R;

public class TaskBtView extends RelativeLayout {

    TextView mTitleTv;
    Spinner mSpinner;
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
        LayoutInflater.from(context).inflate(R.layout.view_task_bt, this, true);
        mTitleTv = (TextView) findViewById(R.id.task_title);
        mSpinner = (Spinner)findViewById(R.id.spinner);

    }

    public void setTitle(String title){
        mTitleTv.setText(title);
    }
}
