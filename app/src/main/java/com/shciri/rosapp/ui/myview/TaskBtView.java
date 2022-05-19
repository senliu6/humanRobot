package com.shciri.rosapp.ui.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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

import com.shciri.rosapp.R;

import java.util.ArrayList;

public class TaskBtView extends RelativeLayout implements View.OnClickListener {

    TextView mTitleTv;
    Spinner mSpinner;
    View mMoreInfo;
    ImageView mEditIv;
    ArrayList<MoreInfoClickListener> mClickListeners = new ArrayList<>();

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
        LayoutInflater.from(context).inflate(R.layout.view_task_bt, this, true);
        mTitleTv = (TextView) findViewById(R.id.task_title);
        mEditIv = (ImageView) findViewById(R.id.editIv);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mMoreInfo = findViewById(R.id.moreInfo);
        mMoreInfo.setOnClickListener(this);
        mEditIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "click Edit ImageView!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setMoreInfoVisibility(int isVisibility) {
        mMoreInfo.setVisibility(isVisibility);
    }

    public void setTitle(String title) {
        mTitleTv.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.moreInfo:
                for (MoreInfoClickListener listener : mClickListeners) {
                    listener.onClick();
                }
                break;
        }
    }

    public void setMoreInfoClickListener(MoreInfoClickListener listener){
        mClickListeners.add(listener);
    }

    public interface MoreInfoClickListener {
        void onClick();
    }
}
