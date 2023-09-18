package com.shciri.rosapp;

import android.app.ADWApiManager;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Message;
import android.util.Log;

import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.data.RosData;
import com.shciri.rosapp.dmros.tool.BatteryPercentChangeEvent;
import com.shciri.rosapp.utils.UartVCP;
import com.shciri.rosapp.utils.protocol.ReplyIPC;

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
    public static UartVCP uartVCP;
    public static ReplyIPC replyIPC;
    public static MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(this, R.raw.disinfecting_warning);  //无需再调用setDataSource
        uartVCP = new UartVCP();
        replyIPC = new ReplyIPC();
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
                replyIPC.getFullData();
                try {
                    Thread.sleep(1);
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
