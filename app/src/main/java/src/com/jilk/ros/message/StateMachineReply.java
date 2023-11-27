package src.com.jilk.ros.message;

/**
 * 功能：
 * 作者：liudz
 * 日期：2023年10月08日
 */
@MessageType(string = "state_machine/state_machine_reply")
public class StateMachineReply extends Message {
    //地图操作
    public byte map_control;
    //导航任务
    public byte navigation_task;
    //硬件操作
    public byte hardware_control;
}
