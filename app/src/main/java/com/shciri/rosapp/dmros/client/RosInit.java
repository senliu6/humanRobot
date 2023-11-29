package com.shciri.rosapp.dmros.client;

import android.util.Log;

import src.com.jilk.ros.ROSClient;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RosInit {
    private ROSBridgeClient client;

    public static boolean isConnect;
    public static boolean offLineMode;

    private OnRosConnectListener connectListener;

    private static RosInit instance;


    public void setOnRosConnectListener(OnRosConnectListener listener) {
        this.connectListener = listener;
    }


    private RosInit() {
        // 私有构造函数，防止外部直接实例化
    }

    public static synchronized RosInit getInstance() {
        if (instance == null) {
            instance = new RosInit();
        }
        return instance;
    }


    public ROSBridgeClient rosConnect(String ip, String port) {
        if (isConnect) {
            return null;
        }
        ConnectionStatus connectionStatus = new ConnectionStatus();
        client = new ROSBridgeClient("ws://" + ip + ":" + port);
        isConnect = client.connect(connectionStatus);
        if (isConnect) {
            return client;
        } else {
            return null;
        }
    }

    public class ConnectionStatus implements ROSClient.ConnectionStatusListener {

        @Override
        public void onConnect() {
            isConnect = true;
            if (connectListener != null) {
                connectListener.onRosConnected(true);
            }
            Log.d("CeshiTAG", "Connected on on on!!!");
        }

        @Override
        public void onDisconnect(boolean normal, String reason, int code) {
            isConnect = false;
            if (connectListener != null) {
                connectListener.onRosConnected(false);
            }
            Log.d("CeshiTAG", "DisConnected !!!" + normal + "  reason:" + reason + "  code:" + code);
        }

        @Override
        public void onError(Exception ex) {
            ex.printStackTrace();
            Log.d("CeshiTAG", "Connect Error on on on!!!");
        }
    }

    public interface OnRosConnectListener {
        void onRosConnected(boolean connected);
    }

}
