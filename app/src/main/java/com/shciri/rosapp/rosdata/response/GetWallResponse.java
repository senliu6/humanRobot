package com.shciri.rosapp.rosdata.response;

import com.shciri.rosapp.rosdata.WallMap;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年12月25日
 */
@MessageType(string = "api_srvs/GetWallMAp")
public class GetWallResponse extends Message {
    public WallMap wall_map;
    public boolean ret;
}
