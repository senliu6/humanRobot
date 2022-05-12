package com.shciri.rosapp.ui.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shciri.rosapp.R;

public class StatusBarView extends LinearLayout {

    public StatusBarView(Context context) {
        super(context);
        initView(context);
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.status_bar, this, true);
    }

}
