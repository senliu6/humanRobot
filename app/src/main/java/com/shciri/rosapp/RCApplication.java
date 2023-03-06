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
    public static String rosIP = "192.168.42.34";
    public static String rosPort = "9090";
    public static SQLiteDatabase db;
    public static String Operator;
    public static byte localBattery;

    @Override
    public void onCreate() {
        super.onCreate();
        adwApiManager = new ADWApiManager(this);
        RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio39/value", 0);
        RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio40/value", 0);
        RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio41/value", 0);
        RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio42/value", 0);
        adwApiManager.SetDeviceTimeZone("Asia/Shanghai");
        String string = adwApiManager.GetDeviceRamSize();
        Log.d("RCApplication","adwApiManager ram = " + string);
        Log.d("RCApplication","adwApiManager ip = " + adwApiManager.GetDeviceIpAddr());

        new Thread(() -> {
            while (true) {
                /*  OUTPUT IO
                   /sys/class/gpio/gpio39/value
                   /sys/class/gpio/gpio40/value
                   /sys/class/gpio/gpio40/value
                   /sys/class/gpio/gpio41/value
                 */
                int l = adwApiManager.GetGpioInputLevel("/sys/devices/virtual/adw/adwdev/adw_human");
                ///Log.v("RCApplication", Integer.toString(l));

                if(l == 1){
//                    EmergencyButton emergencyButton = new EmergencyButton();
//                    emergencyButton.emergency_strike = l == 0;
//                    RosTopic.emergencyButtonTopic.publish(emergencyButton);
//                    RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio39/value", 1);
//                    RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio40/value", 1);
//                    RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio41/value", 1);
//                    RCApplication.adwApiManager.SetGpioOutLevel("/sys/class/gpio/gpio42/value", 1);
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
