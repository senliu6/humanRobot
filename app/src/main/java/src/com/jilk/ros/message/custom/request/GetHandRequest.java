package src.com.jilk.ros.message.custom.request;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年09月13日
 */
@MessageType(string = "rosapi_msgs/GetHandRequest")
public class GetHandRequest extends Message {
    //方向
    public String direction;

}
