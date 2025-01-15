package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;
import src.com.jilk.ros.message.custom.Pose2D;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月26日
 */
@MessageType(string = "common_msgs/Poi")
public class Poi extends Message {
    public long poi_type;
    public long floor_id;
    public String map_name;
    public long zone_id;
    public long poi_id;
    public Pose2D pose;
    public NormalPoICfg normal_Cfg;
    public PackagePoICfg package_poi_cfg;
}
