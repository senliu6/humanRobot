package com.shciri.rosapp;

import android.app.ADWApiManager;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.util.Log;

import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.BatteryPercentChangeEvent;

import org.greenrobot.eventbus.EventBus;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;
import src.com.jilk.ros.message.custom.EmergencyButton;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RCApplication extends Application {
    public static ADWApiManager adwApiManager;
    public static ROSBridgeClient client;
    public String rosIP = "192.168.42.36";
    public String rosPort = "9090";
    public static SQLiteDatabase db;
    public static String Operator;
    public static byte localBattery;

    @Override
    public void onCreate() {
        super.onCreate();
        adwApiManager = new ADWApiManager(this);
        adwApiManager.SetDeviceTimeZone("Asia/Shanghai");
        String string = adwApiManager.GetDeviceRamSize();
        Log.d("RCApplication","adwApiManager ram = " + string);
        Log.d("RCApplication","adwApiManager ip = " + adwApiManager.GetDeviceIpAddr());
        int adc_int = adwApiManager.GetGpioInputLevel("/sys/bus/iio/devices/iio:device0/in_voltage0_raw");
        localBattery = (byte)((adc_int / 1024f * 1.8f * 7 - 10) / (12 - 10) * 100); //because low level battery
        //RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio39/value", 1);
        new Thread(() -> {
            while (true) {
                int l = adwApiManager.GetGpioInputLevel("/sys/devices/virtual/adw/adwdev/adw_human");
                int adc = adwApiManager.GetGpioInputLevel("/sys/bus/iio/devices/iio:device0/in_voltage0_raw");
                ///Log.v("RCApplication", Integer.toString(l));
                //Log.v("RCApplication", Integer.toString(adc));
                localBattery = (byte)(localBattery * 0.9f + (adc / 1024f * 1.8f * 7 - 10) / (12 - 10) * 0.1f * 100);
                if(localBattery > 100) localBattery = 100;
                if(localBattery < 0) localBattery = 0;
                EventBus.getDefault().post(new BatteryPercentChangeEvent(localBattery));
                //Log.v("RCApplication", Float.toString(localBattery));

                if(RosInit.isConnect && RosTopic.emergencyButtonTopic != null){
                    EmergencyButton emergencyButton = new EmergencyButton();
                    emergencyButton.emergency_strike = l == 0;
                    RosTopic.emergencyButtonTopic.publish(emergencyButton);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void pushMessageToIPC() {

    }

    @Override
    public void onTerminate() {
        if(client != null)
            client.disconnect();
        super.onTerminate();
    }

    public ROSBridgeClient getRosClient() {
        return client;
    }

    public void setRosClient(ROSBridgeClient client) {
        this.client = client;
    }
}
