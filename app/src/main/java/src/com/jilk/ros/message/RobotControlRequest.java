package src.com.jilk.ros.message;

@MessageType(string = "path_follower/RobotStatus")
public class RobotControlRequest extends Message{
    public int control;
    /**
     *  ENABLE_ROBOT = 1;
     *  EXECUTE_ROBOT = 2;
     *  PAUSE_ROBOT = 3;
     *  RESET_ROBOT = 4;
     **/
    public RobotControlRequest(int control) {
        this.control = control;
    }
}
