package com.shciri.rosapp.rosdata.response;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月26日
 */

@MessageType(string = "rosapi_srvs/MissionNavigate/Response")
public class MissionResponse extends Message {
    public boolean ret;
}
