package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月25日
 */
@MessageType(string = "common_msgs/TopoEdge")
public class TopoEdge extends Message {
    public long edge_id;
    public short link_type;
    public short link_dir;
    public float way_width;
    public Twist2D max_vel;
    public float edge_weight;
    public long[] link_node_ids;
    public PathData give_path;
}
