package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年05月08日
 */
@MessageType(string = "rosapi_msgs/RobotMissionStatus")
public class Progress extends Message {
    public byte work_mode;
    public byte status_code;
    public byte mission_progress;
    public byte mission_loop_num;

    public String poi_label;
}
