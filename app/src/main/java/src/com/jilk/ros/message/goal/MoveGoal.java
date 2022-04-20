package src.com.jilk.ros.message.goal;

import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;
import src.com.jilk.ros.message.Pose;

@MessageType(string = "geometry_msgs/PoseStamped")
public class MoveGoal extends Message {
    public Header header;
    public Pose pose;
}
