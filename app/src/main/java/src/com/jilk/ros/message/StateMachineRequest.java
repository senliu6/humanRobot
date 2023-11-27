package src.com.jilk.ros.message;

/**
 * 功能：
 * 作者：liudz
 * 日期：2023年10月08日
 */
@MessageType(string = "state_machine/state_machine_request")
public class StateMachineRequest extends Message {
    //地图控制
    public byte map_control;
    //导航任务
    public byte navigation_task;
    public byte hardware_control;
}
