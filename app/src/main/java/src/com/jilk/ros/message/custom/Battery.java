package src.com.jilk.ros.message.custom;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

@MessageType(string = "battery/Battery")
public class Battery extends Message {
    public byte battery_percent;
    public short current;
}
