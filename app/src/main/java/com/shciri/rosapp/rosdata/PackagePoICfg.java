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
@MessageType(string = "common_msgs/PackagePoICfg")
public class PackagePoICfg extends Message {
    public String device_id;
    public String guid_qrcode_id;
    public Pose2D guid_qrcode_global_pose;
    public String final_qrcode_id;
    public Pose2D final_qrcode_relative_pose;
    public int table_height;
}
