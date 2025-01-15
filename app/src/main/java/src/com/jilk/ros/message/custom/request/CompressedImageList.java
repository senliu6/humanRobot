package src.com.jilk.ros.message.custom.request;



import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * @author asus
 */
@MessageType(string = "daimon_hand/CompressedImageList")
public class CompressedImageList extends Message {
    public ImageMessage[] images;
}


