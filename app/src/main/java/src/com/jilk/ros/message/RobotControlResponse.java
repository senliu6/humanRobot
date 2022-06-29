package src.com.jilk.ros.message;

@MessageType(string = "path_follower/RobotStatus")
public class RobotControlResponse extends Message{
    public boolean is_success;
}
