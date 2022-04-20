package src.com.jilk.ros.message;

@MessageType(string = "geometry_msgs/TransformStamped[]")
public class TransformsMsg extends Message {
    public Header header;
    public String child_frame_id;
    public TransformMsg transform;
}
