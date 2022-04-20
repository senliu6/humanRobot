package src.com.jilk.ros.message;

@MessageType(string = "geometry_msgs/Transform")
public class TransformMsg extends Message {
    public Vector3 translation;
    public QuaternionMsg rotation;
}
