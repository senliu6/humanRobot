package com.shciri.rosapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;
import com.shciri.rosapp.RCApplication;

/**
 * 功能：Usb的广播监听
 * 作者：ZhengYuanZhang
 * 日期：2023年07月24日
 */
public class UsbBroadcastReceiver extends BroadcastReceiver {
    private String USB_ = "cn.wch.chromedriver.USB_PERMISSION";
    private String connection = "connected";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (USB_.equals(intent.getAction())) {
            if (intent.getExtras().getBoolean(connection)) {
                // usb 插入
                UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                RCApplication.uartVCP.InitUartVCP(manager);
                Toaster.show(context.getString(R.string.insert));
            } else {
                //   usb 拔出
                UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                RCApplication.uartVCP.InitUartVCP(manager);
                Toaster.show(context.getString(R.string.put_out));
            }
        }
    }
}
