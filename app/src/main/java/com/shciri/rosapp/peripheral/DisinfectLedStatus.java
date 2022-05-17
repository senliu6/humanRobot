package com.shciri.rosapp.peripheral;

public class DisinfectLedStatus {
    static {
        System.loadLibrary("rosapp");
    }
    public native int open();
    public native int close();
    public native int ioctl(int num, int en);
}
