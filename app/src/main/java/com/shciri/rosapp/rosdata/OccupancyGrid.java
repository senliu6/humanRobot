package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.MapMetaData;
import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月25日
 */
@MessageType(string = "nav_msgs/OccupancyGrid")
public  class OccupancyGrid extends Message {
    public Header header;
    public MapMetaData info;
    public byte[] data;
}
