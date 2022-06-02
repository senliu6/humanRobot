package com.shciri.rosapp.dmros.data;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;

import com.shciri.rosapp.dmros.client.RosInit;
import com.shciri.rosapp.dmros.tool.MyPGM;

import src.com.jilk.ros.MessageHandler;
import src.com.jilk.ros.message.CmdVel;
import src.com.jilk.ros.message.CoverageMap;
import src.com.jilk.ros.message.MapMsg;
import src.com.jilk.ros.message.Message;

public class ReceiveHandler {

    MapTopicHandler mapTopicHandler;
    CmdValTopicHandler cmdValTopicHandler;
    TFTopicHandler tfTopicHandler;
    GoalTopicHandler goalTopicHandler;
    CoverageMapHandler coverageMapHandler;

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
        return tfTopicHandler;
    }

    public MessageHandler getCoverageMapTopicHandler() {
        coverageMapHandler = new CoverageMapHandler();
        return coverageMapHandler;
    }
}
