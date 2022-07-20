package src.com.jilk.ros.message.std_msgs;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

@MessageType(string = "std_msgs/MultiArrayDimension")
public class MultiArrayDimension extends Message {
    public String label;
    public int size;
    public int stride;
}
