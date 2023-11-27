package src.com.jilk.ros.message.requestparam;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：抱夹测试参数
 *
 * @author ：liudz
 * 日期：2023年10月20日
 */
@MessageType(string = "state_machine/clamp_control")
public class ClampControl extends Message {
    //点序号
    public byte goal_id;
    //点模式
    public byte plan_mode;
    //上高度
    public short top_height;
    //下高度
    public short bottom_height;
    //抱取 1 抱 2 取
    public byte claw;
    //急停
    public boolean emergency_stop;
    //复位
    public boolean reset;
    //取消导航任务
    public boolean exit_task;
}
