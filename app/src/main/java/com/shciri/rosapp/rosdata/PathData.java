package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;
import src.com.jilk.ros.message.Pose;
import src.com.jilk.ros.message.PoseStamped;
import src.com.jilk.ros.message.custom.Pose2D;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月26日
 */
@MessageType(string = "nav_msgs/Path")
public class PathData extends Message {
    public Header header;
    public PoseStamped[] poses;
}
