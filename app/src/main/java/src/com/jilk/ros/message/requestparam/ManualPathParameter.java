package src.com.jilk.ros.message.requestparam;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;
import src.com.jilk.ros.message.Point;

/**
 * 功能：点集合
 * 日期：2023年10月11日
 * @author liudz
 */
@MessageType(string = "state_machine/manual_path_parameter")
public class ManualPathParameter extends Message {
    //点集合
    public Point[] point;
    public short loop_num;
}
