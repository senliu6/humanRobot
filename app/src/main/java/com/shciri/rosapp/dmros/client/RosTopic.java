package com.shciri.rosapp.dmros.client;

import java.util.List;

import src.com.jilk.ros.MessageHandler;
import src.com.jilk.ros.Topic;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.CoverageMap;
import src.com.jilk.ros.message.CoveragePath;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.goal.MoveGoal;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RosTopic {
    public static src.com.jilk.ros.Topic<CmdVel> cmd_velTopic;
    public static src.com.jilk.ros.Topic<MapMsg> mapTopic;
    public static src.com.jilk.ros.Topic<TFTopic> tfTopic;
    public static src.com.jilk.ros.Topic<MoveGoal> goalTopic;
    public static src.com.jilk.ros.Topic<CoverageMap> coverageMapTopic;
    public static src.com.jilk.ros.Topic<CoveragePath> coveragePathTopic;

    public final String[] TopicName = {"/map","/cmd_vel","/tf","/move_base_simple/goal","/coverage_map","/coverage_path"};
    public boolean[] TopicMatch = new boolean[TopicName.length];

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

    public void initCoverageMapTopic(MessageHandler handler, ROSBridgeClient client) {
        coverageMapTopic  = new Topic<CoverageMap>("/coverage_map", CoverageMap.class, client);
        coverageMapTopic.subscribe(handler);
        coverageMapTopic.advertise();
    }

    public void initCoveragePathTopic(MessageHandler handler, ROSBridgeClient client) {
        coveragePathTopic  = new Topic<CoveragePath>("/coverage_path", CoveragePath.class, client);
        coveragePathTopic.subscribe(handler);
    }

    public void deInitAll() {
        for (int i=0; i<TopicMatch.length; i++) {
            if(TopicMatch[i]){
                switch (i) {
                    case 0:
                        mapTopic.unsubscribe();
                        break;
                    case 1:
                        cmd_velTopic.unadvertise();
                        cmd_velTopic.unsubscribe();
                        break;
                    case 2:
                        tfTopic.unsubscribe();
                        break;
                    case 3:
                        goalTopic.unadvertise();
                        goalTopic.unsubscribe();
                        break;
                    case 4:
                        coverageMapTopic.unadvertise();
                        coverageMapTopic.unsubscribe();
                        break;
                }
            }
        }
    }
}
