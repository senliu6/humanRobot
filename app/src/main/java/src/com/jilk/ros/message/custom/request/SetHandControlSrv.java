package src.com.jilk.ros.message.custom.request;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年09月10日
 */

@MessageType(string = "srv/setHandControlSrv")
public class SetHandControlSrv extends Message {
    //角度数组
    public int[] angle_array;
    //速度数组
    public int[] speed_array;
    //力度数组
    public int[] strength_array;
    //方向
    public String t_direction;

    public byte t_mode; // 0:定位模式 1:伺服模式 2:速度模式 3:力控模式
}

