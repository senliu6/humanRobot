package com.shciri.rosapp.dmros.tool;

public class BatteryEvent {
    public byte percent;
    public short current;

    public BatteryEvent(byte percent, short current) {
        this.percent = percent;
        this.current = current;
    }
}
