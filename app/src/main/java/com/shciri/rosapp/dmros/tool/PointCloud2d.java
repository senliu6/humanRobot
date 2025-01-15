package com.shciri.rosapp.dmros.tool;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;
import src.com.jilk.ros.message.Point;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月28日
 */
@MessageType(string = "common_msgs/PointCloud2d")
public class PointCloud2d extends Message {
    public Point[] points;
}
