package com.shciri.rosapp.ui.control;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.databinding.DialogHealthBinding;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.AirEvent;
import com.shciri.rosapp.dmros.tool.AirQualityEvent;
import com.shciri.rosapp.utils.protocol.RequestIPC;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import src.com.jilk.ros.message.StateNotificationHeartbeat;

public class HealthDialog extends Dialog {


    //控制点击dialog外部是否dismiss
    private boolean isCancelable = true;

    //返回键是否dismiss
    private boolean isCanceledOnTouchOutside = true;

    //Dialog View
    private View view;

    //Dialog弹出位置
    private LocationView locationView = LocationView.CENTER;

    int number = 0;

    private final DialogHealthBinding binding;

    private byte normal = 0X01;

    /**
     * @param context 上下文
     */
    public HealthDialog(Context context) {
        super(context);
        binding = DialogHealthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setBackgroundDrawableResource(R.color.f9fcfc_33);
        binding.conLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Window dialogWindow = getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                /**
                 * 设置这个使dialog全屏
                 */
                lp.width = binding.conLayout.getLeft() + binding.conLayout.getWidth();
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
                binding.conLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        //点击外部是否可以关闭Dialog
        setCancelable(isCancelable);
        //返回键是否可以关闭Dialog
        setCanceledOnTouchOutside(isCanceledOnTouchOutside);

        initView();

        EventBus.getDefault().register(this);
    }

    private void initView() {
        binding.exitIv.setOnClickListener(v -> dismiss());

        //设置最大值（不能设置最小值）
        binding.seekbarFanSpeed.setMax(100);
        //风扇转速初始值为10
        binding.seekbarFanSpeed.setProgress(10);

        // seek bear滑动监听事件
        binding.seekbarFanSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //改变数值
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                number = progress;
                binding.tvRotateSpeed.setText(progress + "%");
                // 如果要设置最小的值的话，我们的最大值要减10（seekbar_fan_speed.setMax(100-10);）
                // progress+=10;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 监听是何时开始滑动
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 监听是何时结束滑动
                byte[] data;
                //最后结束时候的的值传给设备
                data = RequestIPC.fanControlRequest((byte) number);
                RCApplication.uartVCP.sendData(data);
            }
        });


        binding.ornamentalLedSv.setDmSwitchListener(press -> {
            Log.d("ornamental_led", String.valueOf(press));
            byte[] data;
            if (press) {
                data = RequestIPC.disinfectionLedControlRequest((byte) 0, (byte) 0, (byte) 1);
            } else {
                data = RequestIPC.disinfectionLedControlRequest((byte) 0, (byte) 0, (byte) 0);
            }
            RCApplication.uartVCP.sendData(data);
        });

        binding.uvcLedSv.setDmSwitchListener(press -> {
            byte[] data;
            if (press) {
                data = RequestIPC.disinfectionLedControlRequest((byte) 0, (byte) 1, (byte) 1);
            } else {
                data = RequestIPC.disinfectionLedControlRequest((byte) 0, (byte) 0, (byte) 0);
            }
            RCApplication.uartVCP.sendData(data);
        });
        binding.TStatusView.setTitleView(getContext().getString(R.string.temper));
        binding.humidityStatusView.setTitleView(getContext().getString(R.string.humidity));
        binding.formaldehydeStatusView.setTitleView(getContext().getString(R.string.formal));

        initHardware();
    }

    private void initHardware() {
        if (null != RosData.stateNotificationHeartbeat) {
            StateNotificationHeartbeat stateNotificationHeartbeat = RosData.stateNotificationHeartbeat;
            if (stateNotificationHeartbeat.camera_state == normal) {
                binding.healthCameraTextview.setText(R.string.normal);
            } else {
                binding.healthCameraTextview.setText(R.string.noNormal);
            }
            if (stateNotificationHeartbeat.lidar_state == normal) {
                binding.healthRadarTextview.setText(R.string.normal);
            } else {
                binding.healthRadarTextview.setText(R.string.noNormal);
            }
            if (stateNotificationHeartbeat.motor_state == normal) {
                binding.healthMotorTextview.setText(R.string.normal);
            } else {
                binding.healthMotorTextview.setText(R.string.noNormal);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AirEvent event) {
        AirQualityEvent event1 = event.getAirQualityEvent();
        String value = Integer.toString(event1.co2);
        binding.co2View.setValueView(value);
        value = Integer.toString(event1.ch2o);
        binding.formaldehydeStatusView.setValueView(value);
        value = Integer.toString(event1.tvoc);
        binding.TVOCStatusView.setValueView(value);
        value = Integer.toString(event1.pm2p5);
        binding.PM25StatusView.setValueView(value);
        value = Integer.toString(event1.pm10);
        binding.PM10StatusView.setValueView(value);
        value = Float.toString(event1.temperature-272);
        binding.TStatusView.setValueView(value);
        value = Float.toString(event1.humidity);
        binding.humidityStatusView.setValueView(value);
    }

    ;

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
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            window.setAttributes(params);
        }
    }

    public enum LocationView {
        CENTER, TOP, BOTTOM
    }
}