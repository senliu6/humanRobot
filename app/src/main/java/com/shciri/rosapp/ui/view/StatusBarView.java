package com.shciri.rosapp.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.tool.BatteryPercentChangeEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class StatusBarView extends LinearLayout {

    private BatteryView battery;
    private TextView batteryPercentText;
    private TextView timeView;
    private ImageView connectView;
    private ImageView wifistatus0;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            setWifi();
        }
    };


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

        wifistatus0 = findViewById(R.id.wifi0_status);
        setWifi();

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

    /**
     * 判断ifi连接状态
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            Network network = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                network = connectivityManager.getActiveNetwork();
            }
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

            return networkCapabilities != null &&
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        }

        return false;
    }

    //切换WIFI状态的逻辑初版
    public void setWifi(){
        // 用到了我们的本地的WiFi管理
        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        boolean isConnectedToWifi = isWifiConnected(getContext().getApplicationContext());
        if (isConnectedToWifi) {
            int signalStrength = wifiInfo.getRssi();
            // 在这里判断信号强度的大小，根据具体需求处理逻辑，取值范围是0（信号是最好）到-90之间
            Log.e("wifiinfor","信号值为："+signalStrength);
            if (signalStrength<=-90){
                // 注意：需要去设置src去动态改变WiFi图片
                wifistatus0.setImageResource(R.mipmap.wifi_1);
            }else if(signalStrength<=-85){
                wifistatus0.setImageResource(R.mipmap.wifi_2);
            }else if(signalStrength<=-70){
                wifistatus0.setImageResource(R.mipmap.wifi_3);
            }else if(signalStrength<=0){
                wifistatus0.setImageResource(R.mipmap.wifi_4);
            }else{
                wifistatus0.setImageResource(R.mipmap.wifi_0);
                Toaster.showShort( "Wifi Fail");
            }
        }
        // 每隔10秒去重新获取信号值
        handler.sendEmptyMessageDelayed(1,10000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BatteryPercentChangeEvent event) {
        setBatteryPercent(event.getBatteryPercent());
    }
}
