package src.com.jilk.ros.message;

@MessageType(string = "geometry_msgs/Pose")
public class Pose extends Message{
    public Point position;
    public QuaternionMsg orientation;
}
