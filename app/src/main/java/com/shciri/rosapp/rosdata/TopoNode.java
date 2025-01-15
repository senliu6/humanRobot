package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;
import src.com.jilk.ros.message.custom.Pose2D;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月25日
 */

@MessageType(string = "common_msgs/TopoNode")
public class TopoNode extends Message {
    public long node_id;
    public Pose2D pose;
}
