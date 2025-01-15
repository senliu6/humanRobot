package com.shciri.rosapp.rosdata;

import java.util.List;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;
import src.com.jilk.ros.message.Point;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年12月25日
 */
@MessageType(string = "common_msgs/Foldline")
public class Fold extends Message {
    public short wall_type;
    public String wall_id;

    public List<Point> points;
}
