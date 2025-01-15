package src.com.jilk.ros.message;

/**
 * @author asus
 */
@MessageType(string = "geometry_msgs/Point")
public class Point extends Message{
    public double x;
    public double y;
    public double z;

    public Point() {
        // Empty constructor required by ROS serialization
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
