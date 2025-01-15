package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;
import src.com.jilk.ros.message.Point;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月26日
 */
@MessageType(string = "common_msgs/FoldLine")
public class FoldLine extends Message {
    public int wall_type;
    public String wall_id;

    public Point[] points;
}
