package src.com.jilk.ros.message;

@MessageType(string = "tf2_msgs/TFMessage")
public class TFTopic extends Message {
    public TransformsMsg[] transforms;
}
