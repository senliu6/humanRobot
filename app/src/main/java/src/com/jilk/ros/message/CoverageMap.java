package src.com.jilk.ros.message;

@MessageType(string = "nav_msgs/OccupancyGrid")
public class CoverageMap extends Message{
    public Header header;
    public MapMetaData info;
    public byte[] data;
}
