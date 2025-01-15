package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月25日
 */
@MessageType(string = "common_msgs/Twist2D")
public class Twist2D extends Message {
    public double x;
    public double y;
    public double theta;
    public Header header;
}
