package src.com.jilk.ros.message.std_msgs;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

@MessageType(string = "std_msgs/MultiArrayLayout")
public class MultiArrayLayout extends Message {
    public MultiArrayDimension[] dim;
    public int data_offset;
}
