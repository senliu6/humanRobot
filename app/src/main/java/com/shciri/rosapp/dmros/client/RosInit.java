package com.shciri.rosapp.dmros.client;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.shciri.rosapp.dmros.data.RosData;
import org.jetbrains.annotations.NotNull;

import src.com.jilk.ros.MessageHandler;
import src.com.jilk.ros.ROSClient;
import src.com.jilk.ros.Topic;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.Point;
import src.com.jilk.ros.message.Pose;
import src.com.jilk.ros.message.QuaternionMsg;
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.goal.MoveGoal;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RosInit {
    private ROSBridgeClient client;

    public static boolean isConnect;
    public static boolean offLineMode;

    public ROSBridgeClient rosConnect(String ip, String port) {
        if(isConnect)
            return null;
        ConnectionStatus connectionStatus = new ConnectionStatus();
        client = new ROSBridgeClient("ws://" + ip + ":" + port);
        isConnect = client.connect(connectionStatus);
        if(isConnect)
            return client;
        else
            return null;
    }

    public class ConnectionStatus implements ROSClient.ConnectionStatusListener {

        @Override
        public void onConnect() {
            System.out.println("Connected on on on!!!");
        }

        @Override
        public void onDisconnect(boolean normal, String reason, int code) {
            System.out.println("DisConnected !!!" + normal + "  reason:" + reason + "  code:" + code);
        }

        @Override
        public void onError(Exception ex) {
            System.out.println("Connect Error on on on!!!");
        }
    }
}
