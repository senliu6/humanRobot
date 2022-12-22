package src.com.jilk.ros.message.kortex_driver;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

@MessageType(string = "kortex_driver/Base_JointSpeeds")
public class Base_JointSpeeds extends Message {
    public JointSpeed[] joint_speeds;
    public int duration;
}
