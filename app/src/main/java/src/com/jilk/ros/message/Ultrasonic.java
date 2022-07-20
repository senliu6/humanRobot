package src.com.jilk.ros.message;

import src.com.jilk.ros.message.std_msgs.MultiArrayLayout;

@MessageType(string = "std_msgs/Int32MultiArray")
public class Ultrasonic extends Message{
    public MultiArrayLayout layout;
    public int[] data;
}
