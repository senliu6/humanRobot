package src.com.jilk.ros.message;

/**
 * 功能：机器状态实时通知
 * @author ：liudz
 * 日期：2023年10月12日
 */
@MessageType(string = "state_machine/state_notification_heartbeat")
public class StateNotificationHeartbeat extends Message {
    //电机状态
    public byte motor_state;
    //雷达状态
    public byte lidar_state;
    //相机状态
    public byte camera_state;
    //导航状态
    public byte navigation_state;
    //slam状态
    public byte slam_state;
}
