package src.com.jilk.ros.message.custom.res;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2024年09月10日
 */

@MessageType(string = "srv/getHandControlSrv")
public class GetHandControlSrv extends Message {
    //角度数组
    public int[] angle_array;
    //速度数组
    public int[] speed_array;
    //力度数组
    public int[] strength_array;
    //方向
    public String direction;
}

