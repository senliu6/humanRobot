package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月25日
 */
@MessageType(string = "rosapi_msgs/MapCreateCmd")
public class MapCreateCmd extends Message {
    public short func_type;
    public String map_name;
    public String map_path;

}
