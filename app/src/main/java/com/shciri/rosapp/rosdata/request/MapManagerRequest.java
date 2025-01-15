package com.shciri.rosapp.rosdata.request;

import com.shciri.rosapp.rosdata.MapMgrCmd;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月24日
 */
@MessageType(string = "api_srvs/MapManager")
public class MapManagerRequest extends Message {
    public MapMgrCmd cmd;
}
