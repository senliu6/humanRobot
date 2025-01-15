package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月26日
 */
@MessageType(string = "rosapi_msgs/MissionNavigateCmd")
public class MissionNavigateCmd extends Message {
    public short func_type;
    public short mission_type;

    public short motion_type;
    public PoiArray poi_array;
    public short loop_num;
}
