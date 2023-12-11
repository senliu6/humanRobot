package com.shciri.rosapp.dmros.data;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.Log;

import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.tool.BatteryEvent;
import com.shciri.rosapp.dmros.tool.ClampNotifyEvent;
import com.shciri.rosapp.dmros.tool.MyPGM;
import com.shciri.rosapp.dmros.tool.NavPaceEvent;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.dmros.tool.RobotPoseEvent;
import com.shciri.rosapp.dmros.tool.StateNotifyHeadEvent;
import com.shciri.rosapp.dmros.tool.StateTopicReplyEvent;
import com.shciri.rosapp.dmros.tool.UltrasonicEvent;

import org.greenrobot.eventbus.EventBus;

import src.com.jilk.ros.MessageHandler;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.CoverageMap;
import src.com.jilk.ros.message.CoveragePath;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.Pose;
import src.com.jilk.ros.message.StateMachineReply;
import src.com.jilk.ros.message.StateNotificationHeartbeat;
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.TransformsMsg;
import src.com.jilk.ros.message.custom.Battery;
import src.com.jilk.ros.message.custom.ClampNotifyLocation;
import src.com.jilk.ros.message.custom.NavPace;
import src.com.jilk.ros.message.sensor_msgs.Range;

public class ReceiveHandler {

    MapTopicHandler mapTopicHandler = new MapTopicHandler();
    CmdValTopicHandler cmdValTopicHandler = new CmdValTopicHandler();
    TFTopicHandler tfTopicHandler = new TFTopicHandler();
    GoalTopicHandler goalTopicHandler = new GoalTopicHandler();
    CoverageMapHandler coverageMapHandler = new CoverageMapHandler();
    CoveragePathHandler coveragePathHandler = new CoveragePathHandler();
    UltrasonicHandler ultrasonicHandler = new UltrasonicHandler();
    BatteryHandler batteryHandler = new BatteryHandler();
    StatesReplyHandler statesReplyHandler = new StatesReplyHandler();
    StateNotifyHandler stateNotifyHandler = new StateNotifyHandler();
    WatchMapHandler watchMapHandler = new WatchMapHandler();
    RobotPoseHandler robotPoseHandler = new RobotPoseHandler();
    NavPaceHandler navPaceHandler = new NavPaceHandler();
    ClampNotifyLocationHandler clampNotifyLocationHandler = new ClampNotifyLocationHandler();


    private class MapTopicHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            RosData.map = (MapMsg) message;
            RosData.MapData.fastConversion();
            analysisMap(false);
        }
    }

    private void analysisMap(boolean collect) {
        if (RosInit.isConnect) {
            Log.d("CeshiTAG", "---获取地图信息" + PublishEvent.readyPublish);
            MyPGM pgm = new MyPGM();
            int[] pix;
            pix = pgm.readData(RosData.map.info.width, RosData.map.info.height, 5, RosData.map.data, RosData.MapData.poseX, RosData.MapData.poseY);   //P5-Gray image
            Bitmap bitmap = Bitmap.createBitmap(RosData.map.info.width, RosData.map.info.height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pix, 0, RosData.map.info.width, 0, 0, RosData.map.info.width, RosData.map.info.height);
            Matrix invert = new Matrix();
            invert.setScale(1, -1); //镜像翻转以与真实地图对应
            RosData.rosBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), invert, true);

//            System.out.println("MapOK");
            if (collect) {
                if (PublishEvent.readyPublish) {
                    EventBus.getDefault().post(new PublishEvent("/watch/carto_map"));
                }
            } else {
                if (PublishEvent.readyPublish) {
                    EventBus.getDefault().post(new PublishEvent("/map"));
                }
            }

        }
    }

    private class CmdValTopicHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            RosData.cmd_vel = (CmdVel) message;
        }
    }

    private class TFTopicHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            RosData.tf = (TFTopic) message;
            for (TransformsMsg trans : RosData.tf.transforms) {
                if (trans.child_frame_id.equals("base_link")) {
                    RosData.BaseLink.transform = trans.transform;
                    RosData.BaseLink.fastConversion();
                }
            }
            if (PublishEvent.readyPublish) EventBus.getDefault().post(new PublishEvent("/tf"));

        }
    }

    private class GoalTopicHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
        }
    }

    private class CoverageMapHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            RosData.coverageMap = (CoverageMap) message;
        }
    }

    private class CoveragePathHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            RosData.coveragePath = (CoveragePath) message;
            //System.out.println("coveragePath coveragePath coveragePath coveragePath coveragePath coveragePath  ." + RosData.coveragePath.poses[1].header.seq);
            //, (int)(RosData.coveragePath.poseStamped.pose.position.x/0.05f), (int)(RosData.coveragePath.poseStamped.pose.position.x/0.05f))
            if (PublishEvent.readyPublish)
                EventBus.getDefault().post(new PublishEvent("/coverage_path"));
        }
    }

    private class UltrasonicHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            Range ultrasonic = (Range) message;
//            Log.i("xxx",Float.toString(ultrasonic.range));
            if (ultrasonic.range < 0.3) {
                EventBus.getDefault().post(new UltrasonicEvent("warning"));
            }
        }
    }

    private class BatteryHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            Battery battery = (Battery) message;
            EventBus.getDefault().post(new BatteryEvent(battery.battery_percent, battery.current));
        }
    }

    private class StatesReplyHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            StateMachineReply stateMachineReply = (StateMachineReply) message;
            Log.d("CeshiTAG", "statereply" + stateMachineReply.hardware_control + "==" + stateMachineReply.map_control + "==" + stateMachineReply.navigation_task);
            RosData.stateMachineReply = stateMachineReply;
            EventBus.getDefault().post(new StateTopicReplyEvent(stateMachineReply));
        }
    }

    private class StateNotifyHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            StateNotificationHeartbeat stateNotificationHeartbeat = (StateNotificationHeartbeat) message;
//            Log.d("CeshiTAG", "state"+ stateNotificationHeartbeat.motor_state +stateNotificationHeartbeat.camera_state+"=="+ stateNotificationHeartbeat.lidar_state+"="+stateNotificationHeartbeat.navigation_state+"=="+stateNotificationHeartbeat.slam_state);
            RosData.stateNotificationHeartbeat = stateNotificationHeartbeat;
            EventBus.getDefault().post(new StateNotifyHeadEvent(stateNotificationHeartbeat));

        }
    }

    public class WatchMapHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            RosData.map = (MapMsg) message;
            RosData.MapData.fastConversion();
            analysisMap(true);
        }
    }

    public class RobotPoseHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            Pose pose = (Pose) message;
            postDelayed(() -> EventBus.getDefault().post(new RobotPoseEvent(pose)), 500);
        }
    }

    public class NavPaceHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            NavPace pace = (NavPace) message;
            postDelayed(() -> EventBus.getDefault().post(new NavPaceEvent(pace)), 500);
        }
    }

    public class ClampNotifyLocationHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            ClampNotifyLocation location = (ClampNotifyLocation) message;
            postDelayed(() -> EventBus.getDefault().post(new ClampNotifyEvent(location)), 500);
        }
    }


    public MessageHandler getMapTopicHandler() {
        return mapTopicHandler;
    }

    public MessageHandler getCmdValTopicHandler() {
        return cmdValTopicHandler;
    }

    public MessageHandler getTFTopicHandler() {
        return tfTopicHandler;
    }

    public MessageHandler getGoalTopicHandler() {
        return goalTopicHandler;
    }

    public MessageHandler getCoverageMapTopicHandler() {
        return coverageMapHandler;
    }

    public MessageHandler getCoveragePathTopicHandler() {
        return coveragePathHandler;
    }

    public MessageHandler getUltrasonicTopicHandler() {
        return ultrasonicHandler;
    }

    public MessageHandler getBatteryHandler() {
        return batteryHandler;
    }

    public MessageHandler getStatusHandler() {
        return statesReplyHandler;
    }

    public MessageHandler getStatusNotifyHandler() {
        return stateNotifyHandler;
    }

    public MessageHandler getWatchMapHandler() {
        return watchMapHandler;
    }

    public MessageHandler getRobotPoseHandler() {
        return robotPoseHandler;
    }

    public MessageHandler getNavPaceHandler() {
        return navPaceHandler;
    }

    public MessageHandler getClampNotifyHandler() {
        return clampNotifyLocationHandler;
    }


}
