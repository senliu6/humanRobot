package com.shciri.rosapp.dmros.data;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.client.RosTopic;
import com.shciri.rosapp.dmros.tool.MyPGM;

import src.com.jilk.ros.message.std_msgs.MultiArrayDimension;

public class DataProcess {

    MyPGM myPGM = new MyPGM();

    public void moveGoalSend(float x, float y) {
        y = RosData.map.info.height - y;
        x -= RosData.MapData.poseX;
        y -= RosData.MapData.poseY;
        RosData.moveGoal.header.frame_id = "map";
        RosData.moveGoal.pose.position.x = x * 0.05f;
        RosData.moveGoal.pose.position.y = y * 0.05f;
        RosData.moveGoal.pose.orientation.z = 0.98f;
        RosData.moveGoal.pose.orientation.w = -0.019f;
        if (!RosInit.offLineMode && RosTopic.goalTopic != null)
            RosTopic.goalTopic.publish(RosData.moveGoal);
    }

    public void coverageMapProcess(int top, int bottom, int left, int right) {
        if(RosData.map != null) {
            RosData.coverageMap.header = RosData.map.header;
            RosData.coverageMap.info = RosData.map.info;
            RosData.coverageMap.data = myPGM.coverageMapProcess(RosData.map.info.width, RosData.map.info.height, RosData.map.data,
                    RosData.map.info.height-top, RosData.map.info.height-bottom, left, right);
        }
    }

    public void coveragePointsProcess(float top, float bottom, float left, float right) {
        if(RosData.map != null) {
            if(top > bottom) {
                float tmp;
                tmp = top;
                top = bottom;
                bottom = tmp;
            }
            if(left > right) {
                float tmp;
                tmp = left;
                left = right;
                right = tmp;
            }
            RosData.coveragePoints.data = new float[]{left, top,
                    left, bottom,
                    right, bottom,
                    right, top,
                    left, top};
        }
    }
}