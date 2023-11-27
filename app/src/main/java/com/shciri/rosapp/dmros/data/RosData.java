package com.shciri.rosapp.dmros.data;

import android.graphics.Bitmap;
import android.util.Log;

import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.CoverageMap;
import src.com.jilk.ros.message.CoveragePath;
import src.com.jilk.ros.message.CoveragePoints;
import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.MapMetaData;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.Point;
import src.com.jilk.ros.message.Pose;
import src.com.jilk.ros.message.QuaternionMsg;
import src.com.jilk.ros.message.RobotLocation;
import src.com.jilk.ros.message.StateMachineReply;
import src.com.jilk.ros.message.StateNotificationHeartbeat;
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.TimePrimitive;
import src.com.jilk.ros.message.TransformMsg;
import src.com.jilk.ros.message.Ultrasonic;
import src.com.jilk.ros.message.Vector3;
import src.com.jilk.ros.message.goal.MoveGoal;
import src.com.jilk.ros.message.kortex_driver.Base_JointSpeeds;
import src.com.jilk.ros.message.kortex_driver.JointSpeed;
import src.com.jilk.ros.message.std_msgs.MultiArrayDimension;
import src.com.jilk.ros.message.std_msgs.MultiArrayLayout;

public class RosData {
    public static CmdVel cmd_vel;
    public static MapMsg map;
    public static TFTopic tf;
    public static MoveGoal moveGoal;
    public static Bitmap rosBitmap;
    public static CoverageMap coverageMap;
    public static CoveragePath coveragePath;
    public static Ultrasonic ultrasonic;
    public static CoveragePoints coveragePoints;
    public static Base_JointSpeeds jointSpeeds;
    public static byte taskPercent;
    public static int currentMapID = 1;
    public static int dataBaseMaxMapID = 0;

    public static final String MAP = "com.shciri.rosapp.map";
    public static final String TF = "com.shciri.rosapp.tf";
    public static final String TOAST = "com.shciri.rosapp.toast";
    public static final String WATCH = "com.shciri.rosapp.watchmap";


    public static StateMachineReply stateMachineReply;
    public static StateNotificationHeartbeat stateNotificationHeartbeat;

    public static RobotLocation robotLocation;


    public static void RosDataInit() {
        long start = System.currentTimeMillis();
        map = new MapMsg();
        map.info = new MapMetaData();

        cmd_vel = new CmdVel();
        cmd_vel.linear = new Vector3();
        cmd_vel.angular = new Vector3();

        moveGoal = new MoveGoal();
        moveGoal.pose = new Pose();
        moveGoal.pose.position = new Point();
        moveGoal.pose.orientation = new QuaternionMsg();
        moveGoal.header = new Header();

        coverageMap = new CoverageMap();
        coverageMap.header = new Header();
        coverageMap.header.stamp = new TimePrimitive();
        coverageMap.info = new MapMetaData();
        coverageMap.info.origin = new Pose();
        coverageMap.info.map_load_time = new TimePrimitive();

        coveragePoints = new CoveragePoints();
        coveragePoints.layout = new MultiArrayLayout();
        coveragePoints.layout.dim = new MultiArrayDimension[1];
        coveragePoints.layout.dim[0] = new MultiArrayDimension();
        coveragePoints.layout.dim[0].label = "sm";
        coveragePoints.layout.dim[0].size = 10;

        jointSpeeds = new Base_JointSpeeds();
        jointSpeeds.joint_speeds = new JointSpeed[1];
        jointSpeeds.joint_speeds[0] = new JointSpeed();
        jointSpeeds.joint_speeds[0].joint_identifier = 0;//关节轴数
        jointSpeeds.joint_speeds[0].value = 0.3f;
        jointSpeeds.joint_speeds[0].duration = 0;
        long end = System.currentTimeMillis();
        Log.d("CeshiTAG", "耗时" + (end - start));
    }

    public static class BaseLink {
        public static TransformMsg transform;
        public static int x;
        public static int y;
        public static int z;
        public static float pitch;
        public static float yaw;
        public static float roll;

        public static void fastConversion() {
            x = (int) (transform.translation.x / 0.05f);
            y = (int) (transform.translation.y / 0.05f);
            z = (int) (transform.translation.z / 0.05f);
//            Log.d("CeshiTAG", "x=" + x + ", y=" + y + ", z= " + z);
            Quaternion quaternion = new Quaternion((float) transform.rotation.w, (float) transform.rotation.x, (float) transform.rotation.y, (float) transform.rotation.z);
            EulerAngles eulerAngles = quaternion.ToEulerAngles();
            pitch = eulerAngles.pitch;
            yaw = eulerAngles.yaw;
            roll = eulerAngles.roll;
        }
    }

    public static class MapData {
        public static int poseX;  //经过像素点转换的原点坐标
        public static int poseY;

        public static void fastConversion() {
            poseX = (int) (-map.info.origin.position.x / 0.05f);
            poseY = (int) (-map.info.origin.position.y / 0.05f);
        }
    }


    //像素坐标转成世界坐标（单位：m）
    public static Point getActualXY(float x, float y) {
        Point point = new Point();
        point.x = (x - MapData.poseX) * 0.05f;
        point.y = (map.info.height - y - MapData.poseY) * 0.05f;
//        Log.d("CeshiTAG", "转换后的点" + point.x + "y== " + point.y + "原点x=" + MapData.poseX + "Y=" + MapData.poseY + "地图高度" + map.info.height+"X="+x+"y+"+y);
        return point;
    }

    //世界坐标转换成像素坐标
    public static Point getPixelXY(double x, double y) {
        Point point = new Point();
        point.x = x / 0.05f + MapData.poseX;
        point.y = map.info.height - (y / 0.05f) - MapData.poseY;
//        Log.d("CeshiTAG", "转换后的点" + point.x + "y== " + point.y + "原点x=" + MapData.poseX + "Y=" + MapData.poseY + "地图高度" + map.info.height + "X=" + x + "y+" + y);
        return point;
    }
}
