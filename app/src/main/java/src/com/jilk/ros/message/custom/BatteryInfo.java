package src.com.jilk.ros.message.custom;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * @author asus
 */
@MessageType(string = "rosapi_msgs/BatteryInfo")
public class BatteryInfo extends Message {
    public byte battery_percent;
    public boolean is_charging;
    public short current;
}


