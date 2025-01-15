package src.com.jilk.ros.message.requestparam;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年10月25日
 */

@MessageType(string = "msg/StringList")
public class Modalities extends Message {
    public String [] data;
}
