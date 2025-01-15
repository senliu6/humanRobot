package com.shciri.rosapp.dmros.data;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.Log;

import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.tool.AirEvent;
import com.shciri.rosapp.dmros.tool.AirQualityEvent;
import com.shciri.rosapp.dmros.tool.BatteryEvent;
import com.shciri.rosapp.dmros.tool.ClampNotifyEvent;
import com.shciri.rosapp.dmros.tool.GlobalPathEvent;
import com.shciri.rosapp.dmros.tool.MyPGM;
import com.shciri.rosapp.dmros.tool.NavPaceEvent;
import com.shciri.rosapp.dmros.tool.PointCloud2d;
import com.shciri.rosapp.dmros.tool.PointCloudEvent;
import com.shciri.rosapp.dmros.tool.ProgressEvent;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.dmros.tool.RobotPoseEvent;
import com.shciri.rosapp.dmros.tool.StateNotifyHeadEvent;
import com.shciri.rosapp.dmros.tool.StateTopicReplyEvent;
import com.shciri.rosapp.dmros.tool.UltrasonicEvent;
import com.shciri.rosapp.dmros.tool.VideoImageEvent;
import com.shciri.rosapp.dmros.tool.VideoImageIndexEvent;
import com.shciri.rosapp.dmros.tool.VideoImageIndexProEvent;
import com.shciri.rosapp.dmros.tool.VideoImageProEvent;
import com.shciri.rosapp.rosdata.PathData;
import com.shciri.rosapp.rosdata.Progress;

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
import src.com.jilk.ros.message.custom.BatteryInfo;
import src.com.jilk.ros.message.custom.ClampNotifyLocation;
import src.com.jilk.ros.message.custom.NavPace;
import src.com.jilk.ros.message.custom.Pose2D;
import src.com.jilk.ros.message.custom.request.ImageMessage;
import src.com.jilk.ros.message.custom.request.CompressedImageList;
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
    AirHandler airHandler = new AirHandler();

    ImageHandle imageHandler = new ImageHandle();

    ImageHandlePro imageHandlerPro = new ImageHandlePro();

    ImageIndexHandle imageIndexHandle = new ImageIndexHandle();

    ImageIndexHandlePro imageIndexHandlePro = new ImageIndexHandlePro();

    PointCloudHandler pointCloudHandler = new PointCloudHandler();

    ProgressHandler progressHandler = new ProgressHandler();

    GlobalPathHandler globalPathHandler = new GlobalPathHandler();






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
//                if (PublishEvent.readyPublish) {
                    EventBus.getDefault().post(new PublishEvent("/slam_map"));
//                }
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
            BatteryInfo batteryInfo = (BatteryInfo) message;
            postDelayed(() -> EventBus.getDefault().post(new BatteryEvent(batteryInfo)), 500);
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
            Pose2D pose = (Pose2D) message;
            postDelayed(() -> EventBus.getDefault().post(new RobotPoseEvent(pose)), 500);
        }
    }

    public class NavPaceHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            NavPace pace = (NavPace) message;
//            postDelayed(() -> EventBus.getDefault().post(new NavPaceEvent(pace)), 500);
        }
    }

    public class ClampNotifyLocationHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            ClampNotifyLocation location = (ClampNotifyLocation) message;
            postDelayed(() -> EventBus.getDefault().post(new ClampNotifyEvent(location)), 500);
        }
    }

    public class AirHandler extends Handler implements MessageHandler {

        @Override
        public void onMessage(Message message) {
            AirQualityEvent airQualityEvent = (AirQualityEvent) message;
//            Toaster.show("空气质量" + airQualityEvent.co2);
            postDelayed(() -> EventBus.getDefault().post(new AirEvent(airQualityEvent)), 500);
        }
    }

    public class ImageHandle extends Handler implements MessageHandler {

        @Override
        public void onMessage(Message message) {
            CompressedImageList imageMessage = (CompressedImageList) message;
            postDelayed(() -> EventBus.getDefault().post(new VideoImageEvent(imageMessage)), 100);
        }
    }

    public class ImageHandlePro extends Handler implements MessageHandler {

        @Override
        public void onMessage(Message message) {
            ImageMessage imageMessage = (ImageMessage) message;
            postDelayed(() -> EventBus.getDefault().post(new VideoImageProEvent(imageMessage)), 100);
        }
    }

    public class ImageIndexHandle extends Handler implements MessageHandler {

        @Override
        public void onMessage(Message message) {
            ImageMessage imageMessage = (ImageMessage) message;
            postDelayed(() -> EventBus.getDefault().post(new VideoImageIndexEvent(imageMessage)), 100);
        }
    }

    public class ImageIndexHandlePro extends Handler implements MessageHandler {

        @Override
        public void onMessage(Message message) {
//            ImageMessage imageMessage = (ImageMessage) message;
//            postDelayed(() -> EventBus.getDefault().post(new VideoImageIndexProEvent(imageMessage)), 100);
        }
    }



    public class PointCloudHandler extends Handler implements MessageHandler {

        @Override
        public void onMessage(Message message) {
            PointCloud2d pointCloud2d = (PointCloud2d) message;
            postDelayed(() -> EventBus.getDefault().post(new PointCloudEvent(pointCloud2d)), 500);
        }
    }

    public class ProgressHandler extends Handler implements MessageHandler {

        @Override
        public void onMessage(Message message) {
            Progress progress = (Progress) message;
            NavPace navPace = new NavPace();
            navPace.std_msgs = progress.mission_progress;
//            postDelayed(() -> EventBus.getDefault().post(new NavPaceEvent(navPace)), 500);
            postDelayed(() -> EventBus.getDefault().post(new ProgressEvent(progress)), 500);
        }
    }

    public class GlobalPathHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            PathData pathData = (PathData) message;
            Log.d("TTT", "onMessage"+pathData.poses.length);
            postDelayed(() -> EventBus.getDefault().post(new GlobalPathEvent(pathData)), 500);
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

    public MessageHandler getAirHandler() {
        return airHandler;
    }

    public MessageHandler getImageHandler() {
        return imageHandler;
    }

    public MessageHandler getImageHandlerPro() {
        return imageHandlerPro;
    }

    public MessageHandler getImageIndexHandler() {
        return imageIndexHandle;
    }

    public MessageHandler getImageIndexHandlerPro() {
        return imageIndexHandlePro;
    }

    public MessageHandler getPointCloudHandler() {
        return pointCloudHandler;
    }

    public MessageHandler getProgressHandler() {
        return progressHandler;
    }

    public MessageHandler getGlobalPathHandler() {
        return globalPathHandler;
    }
}
