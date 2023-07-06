package com.shciri.rosapp.ui.control;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.AirQualityEvent;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.ui.myview.AirStatusView;
import com.shciri.rosapp.ui.myview.DmSwitchView;
import com.shciri.rosapp.utils.protocol.RequestIPC;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HealthDialog extends Dialog {

    //控制点击dialog外部是否dismiss
    private boolean isCancelable = true;

    //返回键是否dismiss
    private boolean isCanceledOnTouchOutside = true;

    //Dialog View
    private View view;

    //Dialog弹出位置
    private LocationView locationView = LocationView.CENTER;

    private AirStatusView CO2View;
    private AirStatusView FormaldehydeView;
    private AirStatusView TVOCView;
    private AirStatusView PM2_5View;
    private AirStatusView PM10View;
    private AirStatusView TemperatureView;
    private AirStatusView HumidityView;

    private DmSwitchView ornamental_led_sv;
    private DmSwitchView uvc_led_sv;
    private DmSwitchView cooling_fan_sv;

    /**
     * @param context 上下文
     */
    public HealthDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_health);
        findViewById(R.id.exit_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        CO2View = findViewById(R.id.co2_view);
        FormaldehydeView = findViewById(R.id.formaldehyde_status_view);
        TVOCView = findViewById(R.id.TVOC_status_view);
        PM2_5View = findViewById(R.id.PM2_5_status_view);
        PM10View = findViewById(R.id.PM10_status_view);
        TemperatureView = findViewById(R.id.T_status_view);
        HumidityView = findViewById(R.id.humidity_status_view);

        ornamental_led_sv = findViewById(R.id.ornamental_led_sv);
        ornamental_led_sv.setDmSwitchListener(new DmSwitchView.DmSwitchViewListener() {
            @Override
            public void onClick(boolean press) {
                Log.d("ornamental_led", String.valueOf(press));
                byte[] data;
                if(press){
                    data = RequestIPC.disinfectionLedControlRequest((byte) 0, (byte) 0, (byte) 1);
                }else{
                    data = RequestIPC.disinfectionLedControlRequest((byte) 0, (byte) 0, (byte) 0);
                }
                RCApplication.uartVCP.sendData(data);
            }
        });

        uvc_led_sv = findViewById(R.id.uvc_led_sv);
        uvc_led_sv.setDmSwitchListener(new DmSwitchView.DmSwitchViewListener() {
            @Override
            public void onClick(boolean press) {
                byte[] data;
                if(press){
                    data = RequestIPC.disinfectionLedControlRequest((byte) 0, (byte) 1, (byte) 1);
                }else{
                    data = RequestIPC.disinfectionLedControlRequest((byte) 0, (byte) 0, (byte) 0);
                }
                RCApplication.uartVCP.sendData(data);
            }
        });

        cooling_fan_sv = findViewById(R.id.cooling_fan_sv);
        cooling_fan_sv.setDmSwitchListener(new DmSwitchView.DmSwitchViewListener() {
            @Override
            public void onClick(boolean press) {
                byte[] data;
                if(press){
                    data = RequestIPC.fanControlRequest((byte) 50);
                }else{
                    data = RequestIPC.fanControlRequest((byte) 0);
                }
                RCApplication.uartVCP.sendData(data);
            }
        });

        EventBus.getDefault().register(this);
        AirQualityEvent.readyPublish = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AirQualityEvent event) {
        String value = Integer.toString(event.getCO2());
        CO2View.setValueView(value);
        value = Integer.toString(event.getFormaldehyde()/1000);
        FormaldehydeView.setValueView(value);
        value = Integer.toString(event.getTVOC());
        TVOCView.setValueView(value);
        value = Integer.toString(event.getPM2_5());
        PM2_5View.setValueView(value);
        value = Integer.toString(event.getPM10());
        PM10View.setValueView(value);
        value = Float.toString(event.getTemperature());
        TemperatureView.setValueView(value);
        value = Float.toString(event.getHumidity());
        HumidityView.setValueView(value);
    };

    /**
     * 设置是否可以点击 Dialog View 外部关闭 Dialog
     *
     * @param isCancelable true可关闭，false不可关闭
     */
    public void setCancelable(boolean isCancelable) {
        this.isCancelable = isCancelable;
    }

    /**
     * 设置是否可以按返回键关闭 Dialog
     *
     * @param isCanceledOnTouchOutside true可关闭，false不可关闭
     */
    public void setCanceledOnTouchOutside(boolean isCanceledOnTouchOutside) {
        this.isCanceledOnTouchOutside = isCanceledOnTouchOutside;
    }

    @SuppressLint("RtlHardcoded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != view) {
            setContentView(view);
            setCancelable(isCancelable);//点击外部是否可以关闭Dialog
            setCanceledOnTouchOutside(isCanceledOnTouchOutside);//返回键是否可以关闭Dialog
            Window window = this.getWindow();
            assert window != null;
            switch (locationView) {
                case TOP:
                    window.setGravity(Gravity.TOP);
                    break;
                case BOTTOM:
                    window.setGravity(Gravity.BOTTOM);
                    break;
                case CENTER:
                    window.setGravity(Gravity.CENTER);
                    break;
            }
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

    public enum LocationView {
        CENTER, TOP, BOTTOM
    }
}