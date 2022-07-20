package com.shciri.rosapp.dmros.client;

import java.util.List;

import src.com.jilk.ros.MessageHandler;
import src.com.jilk.ros.Topic;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.CoverageMap;
import src.com.jilk.ros.message.CoveragePath;
import src.com.jilk.ros.message.CoveragePoints;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.StartMapping;
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.Ultrasonic;
import src.com.jilk.ros.message.goal.MoveGoal;
import src.com.jilk.ros.message.sensor_msgs.Range;
import src.com.jilk.ros.rosbridge.ROSBridgeClient;

public class RosTopic {
    public static src.com.jilk.ros.Topic<CmdVel> cmd_velTopic;
    public static src.com.jilk.ros.Topic<MapMsg> mapTopic;
    public static src.com.jilk.ros.Topic<TFTopic> tfTopic;
    public static src.com.jilk.ros.Topic<MoveGoal> goalTopic;
    public static src.com.jilk.ros.Topic<CoverageMap> coverageMapTopic;
    public static src.com.jilk.ros.Topic<CoveragePath> coveragePathTopic;
    public static src.com.jilk.ros.Topic<StartMapping> startMappingsTopic;
    public static src.com.jilk.ros.Topic<CoveragePoints> coveragePointsTopic;
    public static src.com.jilk.ros.Topic<Ultrasonic> ultrasonicTopic;
    public static src.com.jilk.ros.Topic<Range> ultrasonicTopic0;
    public static src.com.jilk.ros.Topic<Range> ultrasonicTopic1;
    public static src.com.jilk.ros.Topic<Range> ultrasonicTopic2;
    public static src.com.jilk.ros.Topic<Range> ultrasonicTopic3;

    public final String[] TopicName = {"/map","/cmd_vel","/tf","/move_base_simple/goal","/coverage/points","/coverage/path","/start_mapping", "/ultrasonic/data", "/ultrasonic/sensor0",
        "/ultrasonic/sensor1",
        "/ultrasonic/sensor2",
        "/ultrasonic/sensor3",
    };

    public boolean[] TopicMatch = new boolean[TopicName.length];

    public void subscribeMapTopic(MessageHandler handler, ROSBridgeClient client) {
        mapTopic = new Topic<MapMsg>("/map", MapMsg.class, client);
        mapTopic.subscribe(handler);
    }

    public void subscribeStartMappingTopic(ROSBridgeClient client) {
        startMappingsTopic = new Topic<StartMapping>("/start_mapping", StartMapping.class, client);
        startMappingsTopic.advertise();
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

    public void initCoveragePointsTopic(ROSBridgeClient client) {
        coveragePointsTopic  = new Topic<CoveragePoints>("/coverage/points", CoveragePoints.class, client);
        coveragePointsTopic.advertise();
    }

    public void initCoveragePathTopic(MessageHandler handler, ROSBridgeClient client) {
        coveragePathTopic  = new Topic<CoveragePath>("/coverage/path", CoveragePath.class, client);
        coveragePathTopic.subscribe(handler);
    }

    public void subscribeUltrasonicTopic(MessageHandler handler, ROSBridgeClient client) {
        ultrasonicTopic = new Topic<Ultrasonic>("/ultrasonic/data", Ultrasonic.class, client);
        ultrasonicTopic.subscribe(handler);
    }

    public void subscribeUltrasonicTopic0(MessageHandler handler, ROSBridgeClient client) {
        ultrasonicTopic0 = new Topic<Range>("/ultrasonic/sensor0", Range.class, client);
        ultrasonicTopic0.subscribe(handler);
    }
    public void subscribeUltrasonicTopic1(MessageHandler handler, ROSBridgeClient client) {
        ultrasonicTopic1 = new Topic<Range>("/ultrasonic/sensor1", Range.class, client);
        ultrasonicTopic1.subscribe(handler);
    }
    public void subscribeUltrasonicTopic2(MessageHandler handler, ROSBridgeClient client) {
        ultrasonicTopic2 = new Topic<Range>("/ultrasonic/sensor2", Range.class, client);
        ultrasonicTopic2.subscribe(handler);
    }
    public void subscribeUltrasonicTopic3(MessageHandler handler, ROSBridgeClient client) {
        ultrasonicTopic3 = new Topic<Range>("/ultrasonic/sensor3", Range.class, client);
        ultrasonicTopic3.subscribe(handler);
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
                        coveragePointsTopic.unadvertise();
                        break;
                }
            }
        }
    }
}
