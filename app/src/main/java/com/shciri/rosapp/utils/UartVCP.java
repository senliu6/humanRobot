package com.shciri.rosapp.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;

public class UartVCP {
    private final String TAG = "UartVCP";
    SerialPortFinder serialPortFinder = new SerialPortFinder();
    SerialHelper serialHelper;
    String stm32Device = "/dev/ttyACM1";

    public void InitUartVCP(){
        Log.i(TAG, Arrays.toString(serialPortFinder.getAllDevicesPath()));
        serialHelper = new SerialHelper(stm32Device, 9600) {
            @Override
            protected void onDataReceived(ComBean comBean) {
                Log.e(TAG,  bytesToHexString(comBean.bRec).substring(0, 1));
            }
        };
        serialHelper.setParity(SerialPort.PARITY.ODD.getParity());
        serialHelper.setDataBits(8);
        serialHelper.setStopBits(1);
        try {
            serialHelper.open();

            Log.i(TAG,stm32Device + ": open");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] data){
        Log.d(TAG, "data = " + bytesToHexString(data));
        serialHelper.send(data);
    }

    public String bytesToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder(bArr.length);
        String sTmp;

        for (byte b : bArr) {
            sTmp = Integer.toHexString(0xFF & b);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase());
        }

        return sb.toString();
    }
}
