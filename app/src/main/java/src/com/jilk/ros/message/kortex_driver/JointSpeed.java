package src.com.jilk.ros.message.kortex_driver;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

@MessageType(string = "JointSpeed")
public class JointSpeed extends Message {
    public int joint_identifier;
    public float value;
    public int duration;
}
