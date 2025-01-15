package com.shciri.rosapp.dmros.client;

import android.content.Context;
import android.util.Log;

import com.hjq.toast.Toaster;
import com.shciri.rosapp.R;
import com.shciri.rosapp.dmros.tool.AirQualityEvent;
import com.shciri.rosapp.dmros.tool.PointCloud2d;
import com.shciri.rosapp.rosdata.PathData;
import com.shciri.rosapp.rosdata.Progress;

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
import src.com.jilk.ros.message.custom.BatteryInfo;
import src.com.jilk.ros.message.custom.ClampLocation;
import src.com.jilk.ros.message.custom.ClampNotifyLocation;
import src.com.jilk.ros.message.custom.EmergencyButton;
import src.com.jilk.ros.message.custom.NavPace;
import src.com.jilk.ros.message.custom.Pose2D;
import src.com.jilk.ros.message.custom.request.ImageMessage;
import src.com.jilk.ros.message.custom.request.CompressedImageList;
import src.com.jilk.ros.message.goal.MoveGoal;
import src.com.jilk.ros.message.kortex_driver.Base_JointSpeeds;
import src.com.jilk.ros.message.requestparam.ClampControl;
import src.com.jilk.ros.message.requestparam.ClampHardwareControl;
import src.com.jilk.ros.message.requestparam.EnterManual;
import src.com.jilk.ros.message.requestparam.ManualPathParameter;
import src.com.jilk.ros.message.requestparam.Modalities;
import src.com.jilk.ros.message.requestparam.RequestMapControlParameter;
import src.com.jilk.ros.message.sensor_msgs.Range;
import src.com.jilk.ros.message.std_msgs.StringData;
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
    public static src.com.jilk.ros.Topic<BatteryInfo> batteryInfoTopic;

    public static src.com.jilk.ros.Topic<StateMachineRequest> stateMachineRequestRequestTopic;
    public static src.com.jilk.ros.Topic<StateMachineReply> stateMachineRequestReplyTopic;
    public static src.com.jilk.ros.Topic<StateNotificationHeartbeat> stateNotificationHeartbeatTopic;

    public static src.com.jilk.ros.Topic<ManualPathParameter> manualPathParameterTopic;
    public static src.com.jilk.ros.Topic<RequestMapControlParameter> mapControlParameterTopic;

    public static src.com.jilk.ros.Topic<ClampControl> clampControlTopic;
    public static src.com.jilk.ros.Topic<ClampHardwareControl> clampHardwareControlTopic;

    public static src.com.jilk.ros.Topic<EnterManual> enterManualTopic;

    public static src.com.jilk.ros.Topic<MapMsg> watchMapTopic;

    public static src.com.jilk.ros.Topic<Pose2D> robotPoseTopic;
    public static src.com.jilk.ros.Topic<NavPace> navPaceTopic;

    public static src.com.jilk.ros.Topic<ClampLocation> clampLocationTopic;
    public static src.com.jilk.ros.Topic<ClampNotifyLocation> clampNotifyLocationTopic;

    public static src.com.jilk.ros.Topic<AirQualityEvent> airQualityEventTopic;

    public static src.com.jilk.ros.Topic<Progress> progressTopic;

    public static src.com.jilk.ros.Topic<PathData> globalPath;

    public Topic<CompressedImageList> imageMessageTopic;
    public Topic<ImageMessage> imageMessageTopicPro;
    public Topic<ImageMessage> imageIndexTopic;
    public Topic<StringData> imageIndexTopicPro;

    public static Topic<Modalities> modalitiesTopic;

    public static src.com.jilk.ros.Topic<PointCloud2d> pointCloud2dsTopic;


    public static List<String> TopicName = new ArrayList<>();
    private static Context currentContext;

    static {
        // 初始化 TopicName 集合
        TopicName.add("/slam_map");
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
        TopicName.add("/robot_pose");
        TopicName.add("/watch/carto_map");
        TopicName.add("/nav_pace");
        TopicName.add("/clamp_location");
        TopicName.add("/notify_clamp_location");
        TopicName.add("/air_quality_sensor");
        TopicName.add("/finger_images");
        TopicName.add("/thumb_processed");
        TopicName.add("/index_row");
        TopicName.add("/test");
        TopicName.add("/get_images");
        TopicName.add("/align_point_cloud");//激光点云信息
        TopicName.add("/mission_status");//机器人实时进度
        TopicName.add("/local_path");// 全局路径

    }


    public boolean[] TopicMatch = new boolean[TopicName.size()];

    public void setContext(Context context) {
        currentContext = context;
    }

    public void subscribeMapTopic(MessageHandler handler, ROSBridgeClient client) {
        mapTopic = new Topic<MapMsg>("/slam_map", MapMsg.class, client);
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
        batteryInfoTopic = new Topic<>("/hardware_battery_info", BatteryInfo.class, client);
        batteryInfoTopic.subscribe(handler);
//        batteryInfoTopic.advertise();
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

    public void subscribeWatchMap(MessageHandler handler, ROSBridgeClient client) {
        watchMapTopic = new Topic<MapMsg>("/watch/carto_map", MapMsg.class, client);
        watchMapTopic.subscribe(handler);
    }

    public void subscribeRobotPose(MessageHandler handler, ROSBridgeClient client) {
        robotPoseTopic = new Topic<Pose2D>("/robot_pose", Pose2D.class, client);
        robotPoseTopic.subscribe(handler);
    }

    public void subscribeNavPace(MessageHandler handler, ROSBridgeClient client) {
        navPaceTopic = new Topic<NavPace>("/nav_pace", NavPace.class, client);
        navPaceTopic.subscribe(handler);
    }

    public void advertiseClampLocation(ROSBridgeClient client) {
        clampLocationTopic = new Topic<>("/clamp_location", ClampLocation.class, client);
        clampLocationTopic.advertise();
    }

    public void subscribeClampLocation(MessageHandler handler, ROSBridgeClient client) {
        clampNotifyLocationTopic = new Topic<>("/notify_clamp_location", ClampNotifyLocation.class, client);
        clampNotifyLocationTopic.subscribe(handler);
    }

    public void subscribeAir(MessageHandler handler, ROSBridgeClient client) {
        airQualityEventTopic = new Topic<>("air_quality_sensor", AirQualityEvent.class, client);
        airQualityEventTopic.subscribe(handler);
    }

    public void subscribeVideoFrames(MessageHandler handler, ROSBridgeClient client) {
        imageMessageTopic = new Topic<>("/finger_images", CompressedImageList.class, client);
        imageMessageTopic.subscribe(handler);
    }

    public void subscribeVideoFramesPro(MessageHandler handler, ROSBridgeClient client) {
        imageMessageTopicPro = new Topic<>("/thumb_processed", ImageMessage.class, client);
        imageMessageTopicPro.subscribe(handler);
    }


    public void subscribeIndexVideoFrames(MessageHandler handler, ROSBridgeClient client) {
        imageIndexTopic = new Topic<>("/index_row", ImageMessage.class, client);
        imageIndexTopic.subscribe(handler);
    }

    public void subscribeIndexVideoFramesPro(MessageHandler handler, ROSBridgeClient client) {
        imageIndexTopicPro = new Topic<>("/test", StringData.class, client);
        imageIndexTopicPro.subscribe(handler);//index_processed
    }


    public void subscribeModalitiesControl(ROSBridgeClient client) {
        modalitiesTopic = new Topic<>("/get_images", Modalities.class, client);
        modalitiesTopic.advertise();
    }

    public void subscribePointCloud(MessageHandler handler, ROSBridgeClient client) {
        pointCloud2dsTopic = new Topic<>("/align_point_cloud", PointCloud2d.class, client);
        pointCloud2dsTopic.subscribe(handler);
    }

    public void subscribeProgress(MessageHandler handler, ROSBridgeClient client){
        progressTopic = new Topic<>("/mission_status", Progress.class, client);
        progressTopic.subscribe(handler);
    }

    public void subscribeGlobalPath(MessageHandler handler, ROSBridgeClient client){
        globalPath = new Topic<>("/local_path", PathData.class, client);
        globalPath.subscribe(handler);
    }

    public static void publishStateMachineRequest(StateMachineRequest stateMachineRequest) {
        if (stateMachineRequestRequestTopic != null) {
            stateMachineRequestRequestTopic.publish(stateMachineRequest);
        } else {
            Toaster.showShort(currentContext.getResources().getString(R.string.subscribe_fail_state_request));
        }
    }

    public static void publishControlParameterTopic(RequestMapControlParameter requestMapControlParameter) {
        if (mapControlParameterTopic != null) {
            mapControlParameterTopic.publish(requestMapControlParameter);
        } else {
            Toaster.showShort(currentContext.getResources().getString(R.string.subscribe_fail_map_control));
        }
    }

    public static void publishManualPathParameterTopic(ManualPathParameter manualPathParameter) {
        if (manualPathParameterTopic != null) {
            manualPathParameterTopic.publish(manualPathParameter);
        } else {
            Toaster.showShort(currentContext.getResources().getString(R.string.subscribe_fail_manual_path));
        }
    }

    public static void publishCoveragePointsTopic(CoveragePoints coveragePoints) {
        if (coveragePointsTopic != null) {
            coveragePointsTopic.publish(coveragePoints);
        } else {
            Toaster.showShort(currentContext.getResources().getString(R.string.subscribe_fail_coverage_points));
        }
    }

    public static void publishClampControl(ClampControl control) {
        if (clampControlTopic != null) {
            clampControlTopic.publish(control);
        } else {
            Toaster.showShort(currentContext.getResources().getString(R.string.subscribe_fail_clamp_control));
        }
    }

    public static void publishClampHardWareControl(ClampHardwareControl clampHardwareControl) {
        if (clampHardwareControlTopic != null) {
            clampHardwareControlTopic.publish(clampHardwareControl);
        } else {
            Toaster.showShort(currentContext.getResources().getString(R.string.subscribe_fail_clamp_hardware_control));
        }
    }

    public static void publishEnterManual(EnterManual enterManual) {
        if (enterManualTopic != null) {
            enterManualTopic.publish(enterManual);
        } else {
            Toaster.showShort(currentContext.getResources().getString(R.string.subscribe_fail_enter_manual));
        }
    }

    public static void publishClampLocation(ClampLocation location) {
        if (clampLocationTopic != null) {
            clampLocationTopic.publish(location);
        } else {
            Toaster.showShort(currentContext.getResources().getString(R.string.subscribe_fail_enter_manual));
        }
    }

    public static void publishModalitiesControl(Modalities modalities) {
        if (modalitiesTopic != null) {
            modalitiesTopic.publish(modalities);
        } else {
            Toaster.showShort(currentContext.getResources().getString(R.string.subscribe_fail_clamp_hardware_control));
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
