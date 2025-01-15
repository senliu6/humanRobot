package com.shciri.rosapp.dmros.tool;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

@MessageType(string = "air_quality/AirQualitySensor")
public class AirQualityEvent extends Message {
    public short co2;
    public short ch2o;
    public short tvoc;
    public short pm2p5;
    public short pm10;
    public short temperature;
    public short humidity;
}
