package com.shciri.rosapp.peripheral;

public class Misc {
    static {
        System.loadLibrary("rosapp");
    }
    public native int open();
    public native int close();
    public native int read();
}
