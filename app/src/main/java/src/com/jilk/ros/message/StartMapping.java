package src.com.jilk.ros.message;

@MessageType(string = "/start_mapping")
public class StartMapping extends Message {
    public byte control;
}
