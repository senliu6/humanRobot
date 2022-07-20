package src.com.jilk.ros.message;

import src.com.jilk.ros.message.std_msgs.MultiArrayLayout;

@MessageType(string = "std_msgs/Float32MultiArray")
public class CoveragePoints extends Message{
    public MultiArrayLayout layout;
    public float[] data;
}
