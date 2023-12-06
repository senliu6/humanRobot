package src.com.jilk.ros.message.custom;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：实时任务进度
 *
 * @author ：liudz
 * 日期：2023年11月30日
 */
@MessageType(string = "state_machine/nav_pace")
public class NavPace extends Message {
    //进度，保留2位小数
    public float std_msgs;
}
