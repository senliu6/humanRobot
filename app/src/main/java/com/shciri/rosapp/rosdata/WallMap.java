package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月26日
 */
@MessageType(string = "common_msgs/FoldLineArray")
public class WallMap extends Message {
    public FoldLine[] foldlines;
}
