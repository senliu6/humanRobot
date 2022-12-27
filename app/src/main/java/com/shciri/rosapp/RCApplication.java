package com.shciri.rosapp;

import android.app.ADWApiManager;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import cn.wch.ch34xuartdriver.CH34xUARTDriver;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RCApplication extends Application {
    public static CH34xUARTDriver driver;// 需要将CH34x的驱动类写在APP类下面，使得帮助类的生命周期与整个应用程序的生命周期是相同的
    public static ADWApiManager adwApiManager;
    public static ROSBridgeClient client;
    public String rosIP = "192.168.42.34";
    public String rosPort = "9090";
    public static SQLiteDatabase db;
    public static String Operator;

    @Override
    public void onCreate() {
        super.onCreate();
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
