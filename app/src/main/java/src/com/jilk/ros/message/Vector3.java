package src.com.jilk.ros.message;

@MessageType(string = "geometry_msgs/Vector3")
public class Vector3 extends Message{
    public float x;
    public float y;
    public float z;
}
