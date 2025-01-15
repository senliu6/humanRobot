package src.com.jilk.ros.message.std_msgs;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年10月28日
 */

@MessageType(string = "std_msgs/String")
public class StringData extends Message {
    public String data;
}
