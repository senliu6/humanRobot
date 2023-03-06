package com.shciri.rosapp.utils;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.shciri.rosapp.RCApplication;

public class MyBroadCastReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //TODO something
        } else if(intent.getAction().equals(AlarmManagerUtils.ACTION_PERIOD_CLOCK)){
            Toast.makeText(context.getApplicationContext(), "Alarm Manager just ran", Toast.LENGTH_LONG).show();
            int alarmId = intent.getIntExtra("alarmID", -2);
            boolean fun_switch = intent.getBooleanExtra("fun_switch", false);
            int hour = intent.getIntExtra("hour", -2);
            int minute = intent.getIntExtra("minute", -2);
            Log.d("Alarm","--REPEAT 接收clockId: " + alarmId + "   hour: " + hour + "---minute: " + minute + "---fun_switch: " + fun_switch);
            if(fun_switch){
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio39/value", 1);
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio40/value", 1);
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio41/value", 1);
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio42/value", 1);
            }else{
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio39/value", 0);
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio40/value", 0);
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio41/value", 0);
                RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio42/value", 0);
            }
        }
    }
}