package com.shciri.rosapp.utils;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UartVCP {
    private final String TAG = "UartVCP";
    UsbDevice device = null;
    int stm32VID = 0x0483, stm32PID = 0x5740;
    private UsbSerialPort port;

    public void InitUartVCP(UsbManager manager){
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Log.e(TAG, "get device list  = " + deviceList.size());
        //遍历当前的设备列表
        for (UsbDevice usbDevice : deviceList.values()) {
            device = usbDevice;
            Log.d(TAG, "vid: " + device.getVendorId() + "\t pid: " + device.getProductId());
            if (device.getVendorId() == stm32VID && device.getProductId() == stm32PID) {
                break;
            }
        }
        // 判断设备的ID
        if(device!=null && device.getVendorId()==stm32VID && device.getProductId()==stm32PID){
            getUsbInfo(device);
        }
        else{
            Log.d(TAG,"Don't find desired device.");
        }

        // Find all available drivers from attached devices.
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        for (final UsbSerialDriver driver : availableDrivers) {
            if(driver.getDevice().equals(device)){
                port = driver.getPorts().get(0);
                UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
                if (connection == null) {
                    // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
                    return;
                }
                try {
                    port.open(connection);
                    port.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void sendData(byte[] data) {
        try {
            Log.d(TAG, "data = " + bytesToHexString(data));
            if (null != port){
                port.write(data, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int readData(byte[] response) {
        int len = 0;
        try {
            if (null != port){
                len = port.read(response, 100);
            }
            Log.d(TAG, "len = " + len + "  response = " + bytesToHexString(Arrays.copyOfRange(response, 0, len)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return len;
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

    private void getUsbInfo(UsbDevice usbDevice){
        StringBuilder sb = new StringBuilder();
        if(Build.VERSION.SDK_INT >= 23){
            sb.append(String.format("VID:0x%04X  PID:0x%04X  ManuFN:%s  PN:%s V:%s; DeviceName:%s",
                    usbDevice.getVendorId(),
                    usbDevice.getProductId(),
                    usbDevice.getManufacturerName(),
                    usbDevice.getProductName(),
                    usbDevice.getVersion(),
                    usbDevice.getDeviceName()
            ));
        }
        else {
            sb.append(String.format("VID:%04X  PID:%04X  ManuFN:%s  PN:%s",
                    usbDevice.getVendorId(),
                    usbDevice.getProductId(),
                    usbDevice.getManufacturerName(),
                    usbDevice.getProductName()
            ));
        }
        Log.d(TAG, "Find my STM32 USB Serial Device ~ " + sb.toString());
    }
}
