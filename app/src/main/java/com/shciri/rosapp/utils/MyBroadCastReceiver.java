package com.shciri.rosapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.rosdata.DBUtils;
import com.shciri.rosapp.utils.protocol.RequestIPC;

import src.com.jilk.ros.message.StateMachineRequest;
import src.com.jilk.ros.message.requestparam.ManualPathParameter;

public class MyBroadCastReceiver extends BroadcastReceiver {

    private int alarmIdDefault = -2;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CeshiTAG", intent.getAction().toString());
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //TODO something
        } else if (intent.getAction().equals(AlarmManagerUtils.ACTION_PERIOD_CLOCK)) {
            Toaster.showLong("Alarm Manager just ran");
            int alarmId = intent.getIntExtra("alarmID", alarmIdDefault);
            boolean fun_switch = intent.getBooleanExtra("fun_switch", false);
            boolean led_switch = intent.getBooleanExtra("led_switch", false);
            int hour = intent.getIntExtra("hour", -2);
            int minute = intent.getIntExtra("minute", -2);
            Log.d("Alarm", "--REPEAT 接收clockId: " + alarmId + "   hour: " + hour + "---minute: " + minute + "---fun_switch: " + fun_switch);
            byte[] data;
            if (fun_switch) {
                data = RequestIPC.fanControlRequest((byte) 1);
            } else {
                data = RequestIPC.fanControlRequest((byte) 0);
            }
            RCApplication.uartVCP.sendData(data);

            byte[] dataL;
            if (led_switch) {
                dataL = RequestIPC.disinfectionLedControlRequest((byte) 1, (byte) 1, (byte) 1);
            } else {
                dataL = RequestIPC.disinfectionLedControlRequest((byte) 0, (byte) 0, (byte) 0);
            }
            RCApplication.uartVCP.sendData(dataL);

            if (alarmId != alarmIdDefault) {
                StateMachineRequest stateMachineRequest = new StateMachineRequest();
                stateMachineRequest.navigation_task = 1;
                RosTopic.publishStateMachineRequest(stateMachineRequest);
                ManualPathParameter manualPathParameter = new ManualPathParameter();
//                manualPathParameter.point = DBUtils.getInstance().getPointS(String.valueOf(alarmId));
                manualPathParameter.loop_num = (short) DBUtils.getInstance().getLoopNum(String.valueOf(alarmId));
                RosTopic.publishManualPathParameterTopic(manualPathParameter);
                stateMachineRequest.navigation_task = 3;
                RosTopic.publishStateMachineRequest(stateMachineRequest);
                Toaster.showLong("触发定时任务" + alarmId);
            }
        }
    }
}