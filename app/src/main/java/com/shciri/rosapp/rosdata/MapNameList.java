package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月25日
 */
@MessageType(string = "common_msgs/MapNameList")
public class MapNameList extends Message {
    public String default_map_name;
    public  String[] map_names;
    public OccupancyGrid[] preview_map;

}
