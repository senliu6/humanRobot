package com.shciri.rosapp.ui.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.BatteryPercentChangeEvent;
import com.shciri.rosapp.dmros.tool.PublishEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import src.com.jilk.ros.message.PoseStamped;

public class StatusBarView extends LinearLayout {

    private BatteryView battery;
    private TextView batteryPercentText;
    private TextView timeView;
    private ImageView connectView;

    public int batteryPercent = 100;

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
        EventBus.getDefault().register(this);
        LayoutInflater.from(context).inflate(R.layout.status_bar, this, true);
        battery = findViewById(R.id.battery_status);
        battery.setProgress(batteryPercent);

        batteryPercentText = findViewById(R.id.battery_percent_status);

        timeView = findViewById(R.id.time_status);

        connectView = findViewById(R.id.connect_status);
    }

    public void setBatteryPercent(int batteryPercent) {
        this.batteryPercent = batteryPercent;
        battery.setProgress(batteryPercent);
        String percentString = Integer.toString(batteryPercent) + "%";
        batteryPercentText.setText(percentString);
    }

    public void setTimeView(String time) {
        timeView.setText(time);
    }

    public void setConnectStatus(boolean isConnect) {
        if (isConnect) {
            connectView.setBackgroundResource(R.mipmap.lianjie5_12);
        } else {
            connectView.setBackgroundResource(R.mipmap.choosetask_duankailianjie4_21);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BatteryPercentChangeEvent event) {
        setBatteryPercent(event.getBatteryPercent());
    }
}
