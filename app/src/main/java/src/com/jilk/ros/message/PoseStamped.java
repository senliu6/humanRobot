package src.com.jilk.ros.message;

@MessageType(string = "geometry_msgs/PoseStamped")
public class PoseStamped extends Message{
    public Header header;
    public Pose pose;
}
