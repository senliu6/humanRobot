package com.shciri.rosapp.rosdata.response;


import com.shciri.rosapp.rosdata.MapNameList;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月24日
 */
@MessageType(string = "api_srvs/GetMapNames")
public class GetMapNamesResponse extends Message {
    public boolean ret;
    public MapNameList map_name_list;
}

