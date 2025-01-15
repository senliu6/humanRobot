package com.shciri.rosapp.rosdata.request;

import com.shciri.rosapp.rosdata.MissionNavigateCmd;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月26日
 */
@MessageType(string = "api_srvs/MissionNavigate")
public class MissionNavigateRequest extends Message {
    public MissionNavigateCmd cmd;
}
