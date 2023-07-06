package com.shciri.rosapp.utils.protocol;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class RequestIPC {

    private static int getMyLength(@NotNull Class request) {
        int myLength = 0;
        Field[] fields = request.getFields();
        for (Field field : fields) {
            myLength += HeaderV1.sizeof(field.getType());
        }
        return myLength;
    }


    public static class FanControlRequest extends HandlerInit {
        public byte rotate_speed_percent;

        private final int thisLength = getMyLength(getClass());
        private byte[] struct = new byte[thisLength];

        public byte[] setRequest(byte setRotateSpeedPercent){
            handlerProcess(struct, thisLength, Unpack.FAN_CONTROL_REQUEST_CMD);
            String TAG = "FanControlRequest";
            Log.d(TAG, "length = " + thisLength);
            struct[11] = rotate_speed_percent = setRotateSpeedPercent;
            CRC16.formatFrameBuffer(struct);
            return  struct;
        }
    }
    public static byte[] fanControlRequest(byte setOpen) {
        FanControlRequest request = new FanControlRequest();
        return request.setRequest(setOpen);
    }

    public static class AtmosphereLedControlRequest extends HandlerInit {
        public byte open;

        private final int thisLength = getMyLength(getClass());
        private byte[] struct = new byte[thisLength];

        public byte[] setRequest(byte setOpen){
            handlerProcess(struct, thisLength, Unpack.ATMOSPHERE_LED_CONTROL_REQUEST_CMD);
            String TAG = "AtmosphereLed";
            Log.d(TAG, "length = " + thisLength);
            struct[11] = open = setOpen;
            CRC16.formatFrameBuffer(struct);
            return  struct;
        }
    }
    public static byte[] atmosphereLedControlRequest(byte setOpen) {
        AtmosphereLedControlRequest request = new AtmosphereLedControlRequest();
        return request.setRequest(setOpen);
    }

    public static class DisinfectionLedControlRequest extends HandlerInit {
        public byte open3w;
        public byte open1w;
        public byte openOrnament;

        private final int thisLength = getMyLength(getClass());
        private byte[] struct = new byte[thisLength];

        public byte[] setRequest(byte setOpen3w, byte setOpen1w, byte setOpenOrnament){
            handlerProcess(struct, thisLength, Unpack.DIS_LED_CONTROL_REQUEST_CMD);
            String TAG = "FanControlRequest";
            Log.d(TAG, "length = " + thisLength);
            struct[11] = open3w = setOpen3w;
            struct[12] = open1w = setOpen1w;
            struct[13] = openOrnament = setOpenOrnament;
            CRC16.formatFrameBuffer(struct);
            return  struct;
        }
    }
    public static byte[] disinfectionLedControlRequest(byte setOpen3w, byte setOpen1w, byte setOpenOrnament) {
        DisinfectionLedControlRequest request = new DisinfectionLedControlRequest();
        return request.setRequest(setOpen3w, setOpen1w, setOpenOrnament);
    }

    public static class BatteryRequest extends HandlerInit {

        private final int thisLength = getMyLength(getClass());
        private byte[] struct = new byte[thisLength];

        public byte[] setRequest(){
            handlerProcess(struct, thisLength, Unpack.BATTERY_REQUEST_CMD);
            String TAG = "BatteryRequest";
            Log.d(TAG, "length = " + thisLength);
            CRC16.formatFrameBuffer(struct);
            return  struct;
        }
    }
    public static byte[] batteryRequest() {
        BatteryRequest request = new BatteryRequest();
        return request.setRequest();
    }
}
