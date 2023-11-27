package src.com.jilk.ros.message.requestparam;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：地图处理
 *
 * @author： liudz
 * 日期：2023年10月11日
 */

@MessageType(string = "state_machine/request_map_control_parameter")
public class RequestMapControlParameter extends Message {
    public String map_id;
}
