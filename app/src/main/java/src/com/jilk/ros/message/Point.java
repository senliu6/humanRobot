package src.com.jilk.ros.message;

/**
 * @author asus
 */
@MessageType(string = "geometry_msgs/Point")
public class Point extends Message{
    public double x;
    public double y;
    public double z;
}
