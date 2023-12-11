package src.com.jilk.ros.message.custom;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：夹抱定位输入
 *
 * @author ：liudz
 * 日期：2023年12月11日
 */
@MessageType(string = "state_machine/clamp_location")
public class ClampLocation extends Message {
    //升降电机当前值
    public double lifting_num;
    //夹抱电机当前值
    public double motor_num;
    //左夹抱电机当前值
    public double left_motor_num;
    //右夹抱电机当前值
    public double right_motor_num;
    //开始
    public boolean start;
    //结束
    public boolean stop;

}
