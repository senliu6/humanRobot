package com.shciri.rosapp.dmros.client;

import android.util.Log;

import src.com.jilk.ros.ROSClient;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RosInit {
    private ROSBridgeClient client;

    public static boolean isConnect;
    public static boolean offLineMode;

    public ROSBridgeClient rosConnect(String ip, String port) {
        if(isConnect) {
            return null;
        }
        ConnectionStatus connectionStatus = new ConnectionStatus();
        client = new ROSBridgeClient("ws://" + ip + ":" + port);
        isConnect = client.connect(connectionStatus);
        if(isConnect) {
            return client;
        } else {
            return null;
        }
    }

    public static class ConnectionStatus implements ROSClient.ConnectionStatusListener {

        @Override
        public void onConnect() {
            isConnect = true;
            Log.d("CeshiTAG","Connected on on on!!!");
        }

        @Override
        public void onDisconnect(boolean normal, String reason, int code) {
            isConnect = false;
            Log.d("CeshiTAG","DisConnected !!!" + normal + "  reason:" + reason + "  code:" + code);
        }

        @Override
        public void onError(Exception ex) {
            ex.printStackTrace();
            Log.d("CeshiTAG","Connect Error on on on!!!");
        }
    }
}
