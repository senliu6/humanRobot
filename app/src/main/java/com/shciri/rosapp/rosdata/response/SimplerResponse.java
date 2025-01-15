package com.shciri.rosapp.rosdata.response;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月25日
 */
@MessageType(string = "rosapi_srvs/MapCreate/Response")
public class SimplerResponse extends Message {
    public boolean ret;

}
