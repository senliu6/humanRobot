package com.shciri.rosapp.peripheral;

public class Led {
    static {
        System.loadLibrary("rosapp");
    }
    public native int LedOpen();
    public native int LedClos();
    public native int LedIoctl(int num,int en);
}
