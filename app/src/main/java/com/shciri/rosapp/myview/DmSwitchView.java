package com.shciri.rosapp.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shciri.rosapp.R;

public class DmSwitchView extends RelativeLayout {

    ImageView shangView;
    ImageView xiaView;

    public DmSwitchView(Context context) {
        super(context);
        initView(context);
    }

    public DmSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DmSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_dm_switch, this, true);
        shangView = (ImageView) findViewById(R.id.shangceng_view);
        xiaView = (ImageView) findViewById(R.id.xiaceng_view);

        shangView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shangView.getX() == 0) {
                    shangView.setX(50);
                }else{
                    shangView.setX(0);
                }
            }
        });
    }
}
