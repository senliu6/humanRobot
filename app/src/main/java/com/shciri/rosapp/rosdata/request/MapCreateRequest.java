package com.shciri.rosapp.rosdata.request;

import com.shciri.rosapp.rosdata.MapCreateCmd;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月25日
 */
@MessageType(string = "api_srvs/MapCreate")
public class MapCreateRequest extends Message {
    public MapCreateCmd cmd;
}
