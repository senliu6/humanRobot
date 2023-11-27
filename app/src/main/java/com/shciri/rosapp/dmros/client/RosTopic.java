package com.shciri.rosapp.dmros.client;

import android.util.Log;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;

import java.util.ArrayList;
import java.util.List;

import src.com.jilk.ros.MessageHandler;
import src.com.jilk.ros.Topic;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.CoverageMap;
import src.com.jilk.ros.message.CoveragePath;
import src.com.jilk.ros.message.CoveragePoints;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.Pose;
import src.com.jilk.ros.message.StartMapping;
import src.com.jilk.ros.message.StateMachineReply;
import src.com.jilk.ros.message.StateMachineRequest;
import src.com.jilk.ros.message.StateNotificationHeartbeat;
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.Ultrasonic;
import src.com.jilk.ros.message.custom.Battery;
import src.com.jilk.ros.message.custom.EmergencyButton;
import src.com.jilk.ros.message.goal.MoveGoal;
import src.com.jilk.ros.message.kortex_driver.Base_JointSpeeds;
import src.com.jilk.ros.message.requestparam.ClampControl;
import src.com.jilk.ros.message.requestparam.ClampHardwareControl;
import src.com.jilk.ros.message.requestparam.EnterManual;
import src.com.jilk.ros.message.requestparam.ManualPathParameter;
import src.com.jilk.ros.message.requestparam.RequestMapControlParameter;
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
    public static src.com.jilk.ros.Topic<Base_JointSpeeds> joint_velocity;
    public static src.com.jilk.ros.Topic<EmergencyButton> emergencyButtonTopic;
    public static src.com.jilk.ros.Topic<Battery> batteryTopic;
    public static src.com.jilk.ros.Topic<StateMachineRequest> stateMachineRequestRequestTopic;
    public static src.com.jilk.ros.Topic<StateMachineReply> stateMachineRequestReplyTopic;
    public static src.com.jilk.ros.Topic<StateNotificationHeartbeat> stateNotificationHeartbeatTopic;

    public static src.com.jilk.ros.Topic<ManualPathParameter> manualPathParameterTopic;
    public static src.com.jilk.ros.Topic<RequestMapControlParameter> mapControlParameterTopic;

    public static src.com.jilk.ros.Topic<ClampControl> clampControlTopic;
    public static src.com.jilk.ros.Topic<ClampHardwareControl> clampHardwareControlTopic;

    public static src.com.jilk.ros.Topic<EnterManual> enterManualTopic;

    public static src.com.jilk.ros.Topic<MapMsg> watchMapTopic;

    public static src.com.jilk.ros.Topic<Pose> robotPoseTopic;

    public static List<String> TopicName = new ArrayList<>();

    static {
        // 初始化 TopicName 集合
        TopicName.add("/map");
        TopicName.add("/cmd_vel");
        TopicName.add("/tf");
        TopicName.add("/move_base_simple/goal");
        TopicName.add("/coverage/points");
        TopicName.add("/coverage/path");
        TopicName.add("/my_gen3/in/joint_velocity");
        TopicName.add("/EmergencyButton/external_emergency");
        TopicName.add("/battery");
        TopicName.add("/state_machine_reply");
        TopicName.add("/state_machine_request");
        TopicName.add("/state_notification_heartbeat");
        TopicName.add("/manual_path_parameter");
        TopicName.add("/request_map_control_parameter");
        TopicName.add("/clamp_control");
        TopicName.add("/clamp_hardware_control");
        TopicName.add("/enter_manual_clamp");
        TopicName.add("/robot_location");
        TopicName.add("/watch/carto_map");
    }


    public boolean[] TopicMatch = new boolean[TopicName.size()];

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
        tfTopic = new Topic<TFTopic>("/tf", TFTopic.class, client);
        tfTopic.subscribe(handler);
    }

    public void initGoalTopic(MessageHandler handler, ROSBridgeClient client) {
        goalTopic = new Topic<MoveGoal>("/move_base_simple/goal", MoveGoal.class, client);
        goalTopic.subscribe(handler);
        goalTopic.advertise();
    }

    public void initCoverageMapTopic(MessageHandler handler, ROSBridgeClient client) {
        coverageMapTopic = new Topic<CoverageMap>("/coverage_map", CoverageMap.class, client);
        coverageMapTopic.subscribe(handler);
        coverageMapTopic.advertise();
    }

    public void initCoveragePointsTopic(ROSBridgeClient client) {
        coveragePointsTopic = new Topic<CoveragePoints>("/coverage/points", CoveragePoints.class, client);
        coveragePointsTopic.advertise();
    }

    public void initCoveragePathTopic(MessageHandler handler, ROSBridgeClient client) {
        coveragePathTopic = new Topic<CoveragePath>("/coverage/path", CoveragePath.class, client);
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

    public void subscribeJointVelocityTopic(ROSBridgeClient client) {
        joint_velocity = new Topic<Base_JointSpeeds>("/my_gen3/in/joint_velocity", Base_JointSpeeds.class, client);
        joint_velocity.advertise();
//        jointSpeeds = joint_velocity.take();
        Log.d("SUB Topic", "subscribeJointVelocityTopic");
    }

    public void subscribeEmergencyTopic(ROSBridgeClient client) {
        emergencyButtonTopic = new Topic<EmergencyButton>("/EmergencyButton/external_emergency", EmergencyButton.class, client);
        emergencyButtonTopic.advertise();
        Log.d("SUB Topic", "subscribeEmergencyTopic");
    }

    public void subscribeBatteryTopic(MessageHandler handler, ROSBridgeClient client) {
        batteryTopic = new Topic<Battery>("/battery", Battery.class, client);
        batteryTopic.subscribe(handler);
        batteryTopic.advertise();
        Log.d("SUB Topic", "subscribeBatteryTopic");
    }

    public void subscribeStatesRequestTopic(ROSBridgeClient client) {
        stateMachineRequestRequestTopic = new Topic<>("state_machine_request", StateMachineRequest.class, client);
        stateMachineRequestRequestTopic.advertise();
    }


    public void subscribeStatesReplyTopic(MessageHandler handler, ROSBridgeClient client) {
        Log.d("CeshiTAG", "subscribeStatesReplyTopic");
        stateMachineRequestReplyTopic = new Topic<>("state_machine_reply", StateMachineReply.class, client);
        stateMachineRequestReplyTopic.subscribe(handler);
    }

    public void subscribeStatesNotificationTopic(MessageHandler handler, ROSBridgeClient client) {
        stateNotificationHeartbeatTopic = new Topic<StateNotificationHeartbeat>("state_notification_heartbeat", StateNotificationHeartbeat.class, client);
        stateNotificationHeartbeatTopic.subscribe(handler);
    }

    public void subscribeMapPathTopic(ROSBridgeClient client) {
        manualPathParameterTopic = new Topic<ManualPathParameter>("manual_path_parameter", ManualPathParameter.class, client);
        manualPathParameterTopic.advertise();
    }

    public void subscribeControlPathTopic(ROSBridgeClient client) {
        mapControlParameterTopic = new Topic<RequestMapControlParameter>("/request_map_control_parameter", RequestMapControlParameter.class, client);
        mapControlParameterTopic.advertise();
    }

    public void subscribeClampControl(ROSBridgeClient client) {
        clampControlTopic = new Topic<ClampControl>("/clamp_control", ClampControl.class, client);
        clampControlTopic.advertise();
    }

    public void subscribeClampHardwareControl(ROSBridgeClient client) {
        clampHardwareControlTopic = new Topic<ClampHardwareControl>("/clamp_hardware_control", ClampHardwareControl.class, client);
        clampHardwareControlTopic.advertise();
    }

    public void subscribeEnterManual(ROSBridgeClient client) {
        enterManualTopic = new Topic<EnterManual>("/enter_manual_clamp", EnterManual.class, client);
        enterManualTopic.advertise();
    }

    public void subscribeWatchMap(MessageHandler handler, ROSBridgeClient client){
        watchMapTopic = new Topic<MapMsg>("/watch/carto_map", MapMsg.class, client);
        watchMapTopic.subscribe(handler);
    }

    public void subscribeRobotPose(MessageHandler handler, ROSBridgeClient client){
        robotPoseTopic = new Topic<Pose>("/robot_location", Pose.class, client);
        robotPoseTopic.subscribe(handler);
    }

    public static void publishStateMachineRequest(StateMachineRequest stateMachineRequest) {
        if (stateMachineRequestRequestTopic != null) {
            stateMachineRequestRequestTopic.publish(stateMachineRequest);
        } else {
            Toaster.showShort(R.string.subscribe_fail_state_request);
        }
    }

    public static void publishControlParameterTopic(RequestMapControlParameter requestMapControlParameter) {
        if (mapControlParameterTopic != null) {
            mapControlParameterTopic.publish(requestMapControlParameter);
        } else {
            Toaster.showShort(R.string.subscribe_fail_map_control);
        }
    }

    public static void publishManualPathParameterTopic(ManualPathParameter manualPathParameter) {
        if (manualPathParameterTopic != null) {
            manualPathParameterTopic.publish(manualPathParameter);
        } else {
            Toaster.showShort(R.string.subscribe_fail_manual_path);
        }
    }

    public static void publishCoveragePointsTopic(CoveragePoints coveragePoints) {
        if (coveragePointsTopic != null) {
            coveragePointsTopic.publish(coveragePoints);
        } else {
            Toaster.showShort(R.string.subscribe_fail_coverage_points);
        }
    }

    public static void publishClampControl(ClampControl control) {
        if (clampControlTopic != null) {
            clampControlTopic.publish(control);
        } else {
            Toaster.showShort(R.string.subscribe_fail_clamp_control);
        }
    }

    public static void publishClampHardWareControl(ClampHardwareControl clampHardwareControl) {
        if (clampHardwareControlTopic != null) {
            clampHardwareControlTopic.publish(clampHardwareControl);
        } else {
            Toaster.showShort(R.string.subscribe_fail_clamp_hardware_control);
        }
    }

    public static void publishEnterManual(EnterManual enterManual) {
        if (enterManualTopic != null) {
            enterManualTopic.publish(enterManual);
        } else {
            Toaster.showShort(R.string.subscribe_fail_enter_manual);
        }
    }


    public void deInitAll() {
        for (int i = 0; i < TopicMatch.length; i++) {
            if (TopicMatch[i]) {
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
                    default:
                }
            }
        }
    }
}
