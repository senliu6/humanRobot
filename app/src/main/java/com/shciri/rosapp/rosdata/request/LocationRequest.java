package com.shciri.rosapp.rosdata.request;

import com.shciri.rosapp.rosdata.LocationCalibrateCmd;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年04月29日
 */
@MessageType(string = "api_srvs/LocationCalibrate")
public class LocationRequest extends Message {
    public LocationCalibrateCmd cmd;
}
