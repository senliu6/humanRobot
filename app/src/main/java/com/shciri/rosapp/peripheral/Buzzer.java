
package com.shciri.rosapp.peripheral;

public class Buzzer {
    static {
        System.loadLibrary("rosapp");
    }
    public native int open();
    public native int close();
    public native int ioctl(int num, int en);
}
