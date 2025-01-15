package src.com.jilk.ros.message.custom.request;



import src.com.jilk.ros.message.Header;
import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * @author asus
 */
@MessageType(string = "sensor_msgs/CompressedImage")
public class ImageMessage extends Message {
    public Header header;  // 标准消息头（包含时间戳）
    public int height;     // 图像高度（行数）
    public int width;      // 图像宽度（列数）
    public String encoding; // 图像编码（如 rgb8, bgr8, mono8, 等等）
    public byte is_bigendian; // 图像是否为大端序
    public int step;        // 每一行的字节长度
    public String data;     // 图像数据字节数组
}


