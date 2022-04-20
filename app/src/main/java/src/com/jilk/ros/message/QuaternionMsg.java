package src.com.jilk.ros.message;

@MessageType(string = "geometry_msgs/Quaternion")
public class QuaternionMsg extends Message{
    public double x;
    public double y;
    public double z;
    public double w;
}
