package src.com.jilk.ros.message.requestparam;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：抱夹硬件控制
 *
 * @author ：liudz
 * 日期：2023年10月26日
 */
@MessageType(string = "state_machine/clamp_hardware_cont")
public class ClampHardwareControl extends Message {
    //货叉归零
    public boolean fork_reset;
    //夹抱
    public boolean clip;
    //松开
    public boolean loosen;
    //货叉向前
    public boolean fork_ago;
    //货叉向后
    public boolean fork_after;
    //上升
    public boolean rise;
    //下降
    public boolean decline;
}
