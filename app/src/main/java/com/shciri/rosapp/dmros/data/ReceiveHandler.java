package com.shciri.rosapp.dmros.data;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;

import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.tool.MyPGM;
import com.shciri.rosapp.dmros.tool.PublishEvent;

import org.greenrobot.eventbus.EventBus;

import src.com.jilk.ros.MessageHandler;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.CoverageMap;
import src.com.jilk.ros.message.CoveragePath;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.TFTopic;
import src.com.jilk.ros.message.TransformsMsg;

public class ReceiveHandler {

    MapTopicHandler mapTopicHandler;
    CmdValTopicHandler cmdValTopicHandler;
    TFTopicHandler tfTopicHandler;
    GoalTopicHandler goalTopicHandler;
    CoverageMapHandler coverageMapHandler;
    CoveragePathHandler coveragePathHandler;

    private class MapTopicHandler extends Handler implements MessageHandler {
        @Override
        public void onMessage(Message message) {
            System.out.println("MapTopicHandler");
            RosData.map = (MapMsg)message;
            RosData.MapData.fastConversion();
            analysisMap();
        }
    }

    private void analysisMap() {
        if(RosInit.isConnect) {
            MyPGM pgm = new MyPGM();
            int[] pix;
            pix = pgm.readData(RosData.map.info.width, RosData.map.info.height, 5, RosData.map.data, RosData.MapData.poseX, RosData.MapData.poseY);   //P5-Gray image
            Bitmap bitmap = Bitmap.createBitmap(RosData.map.info.width, RosData.map.info.height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pix, 0, RosData.map.info.width, 0, 0, RosData.map.info.width, RosData.map.info.height);
            Matrix invert = new Matrix();
            invert.setScale(1, -1); //镜像翻转以与真实地图对应
            RosData.rosBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), invert ,true);

            System.out.println("MapOK");
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
            for (TransformsMsg trans :
                    RosData.tf.transforms) {
                if (trans.child_frame_id.equals("base_link")) {
                    RosData.BaseLink.transform = trans.transform;
                    RosData.BaseLink.fastConversion();
                }
            }
            if(PublishEvent.readyPublish)
                EventBus.getDefault().post(new PublishEvent("/tf"));
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
            RosData.coveragePath = (CoveragePath) message ;
//            System.out.println("coveragePath coveragePath coveragePath coveragePath coveragePath coveragePath  ." + RosData.coveragePath.poses[1].header.seq);
            //, (int)(RosData.coveragePath.poseStamped.pose.position.x/0.05f), (int)(RosData.coveragePath.poseStamped.pose.position.x/0.05f))
            if(PublishEvent.readyPublish)
                EventBus.getDefault().post(new PublishEvent("/coverage_path"));
        }
    }


    public MessageHandler getMapTopicHandler() {
        mapTopicHandler = new MapTopicHandler();
        return mapTopicHandler;
    }

    public MessageHandler getCmdValTopicHandler() {
        cmdValTopicHandler = new CmdValTopicHandler();
        return cmdValTopicHandler;
    }

    public MessageHandler getTFTopicHandler() {
        tfTopicHandler = new TFTopicHandler();
        return tfTopicHandler;
    }

    public MessageHandler getGoalTopicHandler() {
        goalTopicHandler = new GoalTopicHandler();
        return goalTopicHandler;
    }

    public MessageHandler getCoverageMapTopicHandler() {
        coverageMapHandler = new CoverageMapHandler();
        return coverageMapHandler;
    }

    public MessageHandler getCoveragePathTopicHandler() {
        coveragePathHandler = new CoveragePathHandler();
        return coveragePathHandler;
    }
}
