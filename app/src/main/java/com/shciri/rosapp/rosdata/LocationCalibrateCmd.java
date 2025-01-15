package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;
import src.com.jilk.ros.message.custom.Pose2D;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月29日
 */
@MessageType(string = "rosapi_msgs/LocationCalibrateCmd")
public class LocationCalibrateCmd extends Message {
    public short func_type;

    public Pose2D refer_pose;


}
