package com.shciri.rosapp.dmros.tool;

public class BatteryPercentChangeEvent {
    public int percent;

    public BatteryPercentChangeEvent(int percent) {
        this.percent = percent;
    }

    public int getBatteryPercent() {
        return percent;
    }
}
