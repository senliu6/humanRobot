package src.com.jilk.ros.message.custom;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

@MessageType(string = "EmergencyButton/emergency")
public class EmergencyButton extends Message {
    public boolean emergency_strike;
}
