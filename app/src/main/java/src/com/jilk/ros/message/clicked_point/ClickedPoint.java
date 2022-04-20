package src.com.jilk.ros.message.clicked_point;

import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;
import src.com.jilk.ros.message.Point;

@MessageType(string = "geometry_msgs/PointStamped")
public class ClickedPoint extends Message {
    public Header header;
    public Point point;
}
