package src.com.jilk.ros.message;

@MessageType(string = "nav_msgs/MapMetaData")
public class MapMetaData extends Message{
    public TimePrimitive map_load_time;
    public float resolution;
    public int width;
    public int height;
    public Pose origin;
}
