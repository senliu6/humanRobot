package com.shciri.rosapp.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shciri.rosapp.R;
import com.shciri.rosapp.peripheral.DisinfectLed;
import com.shciri.rosapp.peripheral.DisinfectLedStatus;
import com.shciri.rosapp.peripheral.Led;

public class DmSwitchView extends RelativeLayout {

    ImageView shangView;
    ImageView xiaView;
    int ledStatus;

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

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_dm_switch, this, true);
        shangView = (ImageView) findViewById(R.id.shangceng_view);
        xiaView = (ImageView) findViewById(R.id.xiaceng_view);
    }

    public void connectLed() {
        Led led = new Led();
        DisinfectLed disinfectLed = new DisinfectLed();
        DisinfectLedStatus disinfectLedStatus = new DisinfectLedStatus();

        disinfectLed.open();
        disinfectLedStatus.open();

        if(led.LedOpen() == -1)
            Toast.makeText(getContext(), "设备打开失败！ ", Toast.LENGTH_SHORT).show();
        else
            led.LedIoctl(1,1);

        xiaView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shangView.getX() == 0) {
                    shangView.setX(50);
                    led.LedIoctl(0,0);
                    int level = disinfectLedStatus.ioctl(0, ledStatus);
                    System.out.println("level = " + level + "  status = "+ ledStatus);
                }else{
                    shangView.setX(0);
                    led.LedIoctl(1,1);
                    int level = disinfectLedStatus.ioctl(0, ledStatus);
                    System.out.println("level = " + level + "  status = "+ ledStatus);
                }
            }
        });
    }
}
