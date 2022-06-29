package src.com.jilk.ros.message;

@MessageType(string = "nav_msgs/Path")
public class CoveragePath extends Message {
    public Header header;
    public PoseStamped[] poses;
}
