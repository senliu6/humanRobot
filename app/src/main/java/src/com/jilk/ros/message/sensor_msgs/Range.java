package src.com.jilk.ros.message.sensor_msgs;

import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

@MessageType(string = "sensor_msgs/Range")
public class Range extends Message {
    public Header header;
    public byte radiation_type;
    public float field_of_view;
    public float min_range;
    public float max_range;
    public float range;
}
