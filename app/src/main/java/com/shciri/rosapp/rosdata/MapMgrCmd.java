package com.shciri.rosapp.rosdata;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月25日
 */
@MessageType(string = "rosapi_msgs/MapManagerCmd")
public class MapMgrCmd extends Message {
    public short func_type;
    public String map_name;
    public String map_rename;
    public TopoGraph topo_graph;

    public WallMap wall_map;

    public PoiMap poi_map;


}
