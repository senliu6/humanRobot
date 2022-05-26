package com.shciri.rosapp.dmros.client;

import src.com.jilk.ros.MessageHandler;
import src.com.jilk.ros.Topic;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.goal.MoveGoal;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RosTopic {
    public static src.com.jilk.ros.Topic<CmdVel> cmd_velTopic;
    public static src.com.jilk.ros.Topic<MapMsg> mapTopic;
    public static src.com.jilk.ros.Topic<TFTopic> tfTopic;
    public static src.com.jilk.ros.Topic<MoveGoal> goalTopic;

    public void subscribeMapTopic(MessageHandler handler, ROSBridgeClient client) {
        mapTopic = new Topic<MapMsg>("/map", MapMsg.class, client);
        mapTopic.subscribe(handler);
    }

    public void subscribeCmdVelTopic(MessageHandler handler, ROSBridgeClient client) {
        cmd_velTopic = new Topic<CmdVel>("/cmd_vel", CmdVel.class, client);
        cmd_velTopic.subscribe(handler);
        cmd_velTopic.advertise();
    }

    public void subscribeTFTopic(MessageHandler handler, ROSBridgeClient client) {
        tfTopic  = new Topic<TFTopic>("/tf", TFTopic.class, client);
        tfTopic.subscribe(handler);
    }

    public void initGoalTopic(MessageHandler handler, ROSBridgeClient client) {
        goalTopic  = new Topic<MoveGoal>("/move_base_simple/goal", MoveGoal.class, client);
        goalTopic.subscribe(handler);
        goalTopic.advertise();
    }
}
