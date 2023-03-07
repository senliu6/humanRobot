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
        public byte open;

        private final int thisLength = getMyLength(getClass());
        private byte[] struct = new byte[thisLength];

        public byte[] setRequest(byte setOpen){
            handlerProcess(struct, thisLength);
            String TAG = "FanControlRequest";
            Log.d(TAG, "length = " + thisLength);
            struct[11] = open = setOpen;
            CRC16.formatFrameBuffer(struct);
            return  struct;
        }
    }
    public static byte[] fanControlRequest(byte setOpen) {
        FanControlRequest request = new FanControlRequest();
        return request.setRequest(setOpen);
    }
}
