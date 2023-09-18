package com.shciri.rosapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import com.shciri.rosapp.RCApplication;

/**
 * 功能：Usb的广播监听
 * 作者：ZhengYuanZhang
 * 日期：2023年07月24日
 */
public class UsbBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals("cn.wch.wchusbdriver.USB_PERMISSION")){
             if (intent.getExtras().getBoolean("connected")){
                 // usb 插入
                 UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                 RCApplication.uartVCP.InitUartVCP(manager);
                 Toast.makeText(context, "插入", Toast.LENGTH_LONG).show();
             }else{
                 //   usb 拔出
                 UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                 RCApplication.uartVCP.InitUartVCP(manager);
                 Toast.makeText(context, "拔出", Toast.LENGTH_LONG).show();
             }
        }
    }
}
