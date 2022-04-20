package src.com.jilk.ros.message;

@MessageType(string = "geometry_msgs/Twist")
public class CmdVel extends Message{
    public Vector3 linear;
    public Vector3 angular;
}
