package com.shciri.rosapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.shciri.rosapp.data.RosData;
import com.shciri.rosapp.myfragment.HomeFragment;

import org.jetbrains.annotations.NotNull;

import src.com.jilk.ros.ROSClient;
import src.com.jilk.ros.Topic;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.Point;
import src.com.jilk.ros.message.Pose;
import src.com.jilk.ros.message.QuaternionMsg;
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.TransformsMsg;
import src.com.jilk.ros.message.goal.MoveGoal;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RosInit {
    public static src.com.jilk.ros.Topic<CmdVel> cmd_velTopic;
    public static src.com.jilk.ros.Topic<MapMsg> mapTopic;
    public static src.com.jilk.ros.Topic<TFTopic> tfTopic;
    public static src.com.jilk.ros.Topic<MoveGoal> goalTopic;

    public static boolean isConnect;
    public static boolean offLineMode;

    public static LocalBroadcastManager localBroadcastManager;

    private Context context;

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

    public RosInit(@NotNull Context context) {
        this.context = context;
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public void rosConnect() {
        if(isConnect)
            return;

        ConnectionStatus connectionStatus = new ConnectionStatus();
        ROSBridgeClient client = new ROSBridgeClient("ws://192.168.1.104:9090");
        isConnect = client.connect();
        if(isConnect){
            initTopic(client);
        }
    }

    public void initTopic(ROSBridgeClient client) {
        cmd_velTopic = new Topic<CmdVel>("/cmd_vel", CmdVel.class, client);
        mapTopic = new Topic<MapMsg>("/map", MapMsg.class, client);
        tfTopic  = new Topic<TFTopic>("/tf", TFTopic.class, client);
        goalTopic  = new Topic<MoveGoal>("/move_base_simple/goal", MoveGoal.class, client);
        cmd_velTopic.subscribe();
        mapTopic.subscribe();
        tfTopic.subscribe();
        try {
            RosData.cmd_vel = cmd_velTopic.take(); //上面调用的没有handler的subcribe()，因此需要自行调用Topic实例对象的take()来取出预订的来自RosBridge WebSocket Server的消息
            RosData.map = mapTopic.take();
            RosData.moveGoal = new MoveGoal();
            RosData.moveGoal.pose = new Pose();
            RosData.moveGoal.pose.position = new Point();
            RosData.moveGoal.pose.orientation = new QuaternionMsg();
            RosData.moveGoal.header = new Header();
        }
        catch (InterruptedException ex) {}
        RosData.cmd_vel.print();
        cmd_velTopic.advertise();

        RosData.MapData.fastConversion();

        System.out.println("h=" + RosData.map.info.height);
        System.out.println("w=" + RosData.map.info.width);
        System.out.println("resolution=" + RosData.map.info.resolution);
        System.out.println("position.x =" + RosData.map.info.origin.position.x);
        System.out.println("position.y =" + RosData.map.info.origin.position.y);
        System.out.println("position.z =" + RosData.map.info.origin.position.z);

        Intent intent = new Intent(RosData.MAP);
        localBroadcastManager.sendBroadcast(intent);
    }

    public void getTF(){
        new Thread(() -> {
            while(true) {
                if (isConnect) {
                    try {
                        Thread.sleep(1);
                        RosData.tf = tfTopic.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (TransformsMsg trans :
                            RosData.tf.transforms) {
                        if (trans.child_frame_id.equals("base_link")) {
                            RosData.BaseLink.transform = trans.transform;
                            RosData.BaseLink.fastConversion();
                            Intent intent = new Intent(RosData.TF);
                            localBroadcastManager.sendBroadcast(intent);
                        }
                    }
                }
            }
        }).start();
    }

    public void getMap(){
        new Thread(() -> {
            while(true) {
                if (isConnect) {
                    try {
                        Thread.sleep(200);
                        RosData.map = mapTopic.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(RosData.MAP);
                    localBroadcastManager.sendBroadcast(intent);
                }
            }
        }).start();
    }
}
