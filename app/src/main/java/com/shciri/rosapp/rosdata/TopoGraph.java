package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月25日
 */
@MessageType(string = "common_msgs/TopoGraph")
public class TopoGraph extends Message {
    public TopoNode[] nodes;
    public TopoEdge[] edges;
}
