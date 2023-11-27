package src.com.jilk.ros.message.requestparam;

import src.com.jilk.ros.message.Message;
import src.com.jilk.ros.message.MessageType;

/**
 * 功能：
 *
 * @author ：liudz
 * 日期：2023年10月30日
 */
@MessageType(string = "state_machine/enter_manual_clamp")
public class EnterManual extends Message {
    public boolean enter_manual;
}
