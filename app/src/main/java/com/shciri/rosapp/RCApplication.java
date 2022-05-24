package com.shciri.rosapp;

import android.app.Application;

import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RCApplication extends Application {
    ROSBridgeClient client;
    public String rosIP = "192.168.1.108";
    public String rosPort = "9090";

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
