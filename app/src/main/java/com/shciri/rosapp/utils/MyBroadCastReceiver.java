package com.shciri.rosapp.utils;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.utils.protocol.RequestIPC;

public class MyBroadCastReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //TODO something
        } else if(intent.getAction().equals(AlarmManagerUtils.ACTION_PERIOD_CLOCK)){
            Toast.makeText(context.getApplicationContext(), "Alarm Manager just ran", Toast.LENGTH_LONG).show();
            int alarmId = intent.getIntExtra("alarmID", -2);
            boolean fun_switch = intent.getBooleanExtra("fun_switch", false);
            boolean led_switch = intent.getBooleanExtra("led_switch", false);
            int hour = intent.getIntExtra("hour", -2);
            int minute = intent.getIntExtra("minute", -2);
            Log.d("Alarm","--REPEAT 接收clockId: " + alarmId + "   hour: " + hour + "---minute: " + minute + "---fun_switch: " + fun_switch);
            if(fun_switch){
                byte[] data = RequestIPC.fanControlRequest((byte)1);
                RCApplication.uartVCP.sendData(data);
            }else{
                byte[] data = RequestIPC.fanControlRequest((byte)0);
                RCApplication.uartVCP.sendData(data);
            }

            if(led_switch){
                byte[] data = RequestIPC.disinfectionLedControlRequest((byte)1,(byte)1,(byte)1);
                RCApplication.uartVCP.sendData(data);
            }else{
                byte[] data = RequestIPC.disinfectionLedControlRequest((byte)0,(byte)0,(byte)0);
                RCApplication.uartVCP.sendData(data);
            }
        }
    }
}