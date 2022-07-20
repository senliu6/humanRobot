package com.shciri.rosapp.dmros.data;

import android.graphics.Bitmap;

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
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.TimePrimitive;
import src.com.jilk.ros.message.TransformMsg;
import src.com.jilk.ros.message.Ultrasonic;
import src.com.jilk.ros.message.goal.MoveGoal;
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
    public static byte taskPercent;
    public static int currentMapID = 1;
    public static int dataBaseMaxMapID = 0;

    public static final String MAP = "com.shciri.rosapp.map";
    public static final String TF = "com.shciri.rosapp.tf";
    public static final String TOAST = "com.shciri.rosapp.toast";

    public static void RosDataInit() {
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
            x = (int)(transform.translation.x/0.05f);
            y = (int)(transform.translation.y/0.05f);
            z = (int)(transform.translation.z/0.05f);
            Quaternion quaternion = new Quaternion((float)transform.rotation.w, (float)transform.rotation.x, (float)transform.rotation.y, (float)transform.rotation.z);
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
            poseX = (int)(-map.info.origin.position.x/0.05f);
            poseY = (int)(-map.info.origin.position.y/0.05f);
        }
    }
}
