package src.com.jilk.ros.message;

/**
 * 功能：机器人实时位置
 *
 * @author ：liudz
 * 日期：2023年11月15日
 */
@MessageType(string = "state_machine/robot_location")
public class RobotLocation extends Message {
    public double x;
    public double y;
}
