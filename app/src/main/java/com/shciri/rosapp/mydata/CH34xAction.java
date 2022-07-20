package com.shciri.rosapp.mydata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.shciri.rosapp.RCApplication;
import com.shciri.rosapp.dmros.tool.AirQualityEvent;
import com.shciri.rosapp.dmros.tool.BatteryPercentChangeEvent;
import com.shciri.rosapp.dmros.tool.PublishEvent;
import com.shciri.rosapp.ui.myview.StatusBarView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class CH34xAction {

    public byte[] writeBuffer;
    public byte[] readBuffer;
    private static String readString = "";
    private boolean isOpen;
    private Handler handler;

    public int baudRate = 9600;
    public byte stopBit = 1;
    public byte dataBit = 8;
    public byte parity = 0;
    public byte flowControl = 0;

    private Context context;
    private StatusBarView statusBarView;

    public CH34xAction(@NotNull Context obj, StatusBarView statusBar) {
        context = obj;
//        statusBarView = statusBar;
        if (!RCApplication.driver.UsbFeatureSupported())// 判断系统是否支持USB HOST
        {
            Dialog dialog = new AlertDialog.Builder(obj)
                    .setTitle("提示")
                    .setMessage("您的手机不支持USB HOST，请更换其他手机再试！")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            System.exit(0);
                        }
                    }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        writeBuffer = new byte[512];
        readBuffer = new byte[512];
        isOpen = false;

        int retVal = RCApplication.driver.ResumeUsbPermission();
        if(retVal == 0) {
            retVal = RCApplication.driver.ResumeUsbList();
            if(retVal == -1) {
                Toast.makeText(obj, "Open CH34x failed!", Toast.LENGTH_SHORT).show();
                RCApplication.driver.CloseDevice();
            }else if(retVal == 0) {
                if(RCApplication.driver.mDeviceConnection != null) {
                    if(!RCApplication.driver.UartInit()) {
                        Toast.makeText(obj, "Initialization failed!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(obj, "Device opened", Toast.LENGTH_SHORT).show();
                    isOpen = true;
                    new readThread().start();
                }
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(obj);
                builder.setTitle("未授权限");
                builder.setMessage("确认退出吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                });
                builder.show();
            }
        }

        if(isOpen) {
            if (RCApplication.driver.SetConfig(baudRate, dataBit, stopBit, parity,//配置串口波特率，函数说明可参照编程手册
                    flowControl)) {
                Toast.makeText(obj, "Config successfully",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(obj, "Config failed!",
                        Toast.LENGTH_SHORT).show();
            }
        }

        handler = new readHandler();
    }

    public void queryBatteryInfo() {
        //byte[] cmd = {0x01, 0x03, 0x00, 0x00, 0x00, 0x2A, (byte)0xC4, 0x15};
        if(RCApplication.driver.isConnected()) {
            byte[] cmd = {0x01, 0x03, 0x00, 0x14, 0x00, 0x01, (byte) 0xC4, 0x0E};
            writeByte(cmd);
        }
    }

    public void queryAirQuality() {
        if(RCApplication.driver.isConnected()) {
            byte[] cmd = {0x07, 0x03, 0x00, 0x02, 0x00, 0x07, (byte)0xA5, (byte)0xAE};
            writeByte(cmd);
        }
    }

    private void frameUnpack() {
        readBuffer = toByteArray(readString);
        if(readBuffer.length == 7) {
            if (readBuffer[0] == 0x01 && readBuffer[1] == 0x03 && readBuffer[2] == 0x02) {
//                System.out.println("frameUnpack = " + readBuffer[3]);
//                System.out.println("frameUnpack = " + ((byte)readBuffer[4] & 0xff));
                float batteryPercent = ((((readBuffer[3] << 8) | ((byte)readBuffer[4] & 0xff))) / 4000f) * 1000f + 5; //四舍五入
                EventBus.getDefault().post(new BatteryPercentChangeEvent((int)batteryPercent / 10));
            }
        }else if(readBuffer.length == 19) {
            if (readBuffer[0] == 0x07 && readBuffer[1] == 0x03 && readBuffer[2] == 0x0E) {
                if(AirQualityEvent.readyPublish) {
                    EventBus.getDefault().post(new AirQualityEvent(((readBuffer[3] & 0xFF) << 8) + (readBuffer[4] & 0xFF),
                            ((readBuffer[5] & 0xFF) << 8) + (readBuffer[6] & 0xFF),
                            ((readBuffer[7] & 0xFF) << 8) + (readBuffer[8] & 0xFF),
                            ((readBuffer[9] & 0xFF) << 8) + (readBuffer[10] & 0xFF),
                            ((readBuffer[11] & 0xFF) << 8) + (readBuffer[12] & 0xFF),
                            (((readBuffer[13] & 0xFF) << 8) + (readBuffer[14] & 0xFF)) / 10f,
                            (((readBuffer[15] & 0xFF) << 8) + (readBuffer[16] & 0xFF)) / 10f));
                }
            }
        }
    }

    public void writeByte(byte[] send) {
        int retVal = RCApplication.driver.WriteData(send, send.length);//写数据，第一个参数为需要发送的字节数组，第二个参数为需要发送的字节长度，返回实际发送的字节长度
        if (retVal < 0)
            Toast.makeText(context, "Write failed!", Toast.LENGTH_SHORT).show();
    }

    private class readHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            readString = (String) msg.obj;
//            System.out.println("readString = " + readString);
            frameUnpack();
        }
    }

    private class readThread extends Thread {

        public void run() {

            byte[] buffer = new byte[4096];

            while (true) {
                Message msg = Message.obtain();
                if (!isOpen) {
                    break;
                }
                int length = RCApplication.driver.ReadData(buffer, 4096);
                if (length > 0) {
                    String recv = toHexString(buffer, length);		//以16进制输出
//					String recv = new String(buffer, 0, length);		//以字符串形式输出
                    msg.obj = recv;
                    handler.sendMessage(msg);
                }
            }
        }
    }
    /**
     * 将byte[]数组转化为String类型
     * @param arg    需要转换的byte[]数组
     * @param length 需要转换的数组长度
     * @return 转换后的String队形
     **/
    private String toHexString(byte[] arg, int length) {
        String result = new String();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result = result
                        + (Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])) + " ";
            }
            return result;
        }
        return "";
    }
    /**
     * 将String转化为byte[]数组
     * @param arg
     *            需要转换的String对象
     * @return 转换后的byte[]数组
     */
    private byte[] toByteArray(String arg) {
        if (arg != null) {
            /* 1.先去除String中的' '，然后将String转换为char数组 */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }
            /* 将char数组中的值转成一个实际的十进制数组 */
            int EvenLength = (length % 2 == 0) ? length : length + 1;
            if (EvenLength != 0) {
                int[] data = new int[EvenLength];
                data[EvenLength - 1] = 0;
                for (int i = 0; i < length; i++) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i] - '0';
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i] - 'a' + 10;
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i] - 'A' + 10;
                    }
                }
                /* 将 每个char的值每两个组成一个16进制数据 */
                byte[] byteArray = new byte[EvenLength / 2];
                for (int i = 0; i < EvenLength / 2; i++) {
                    byteArray[i] = (byte) (data[i * 2] * 16 + data[i * 2 + 1]);
                }
                return byteArray;
            }
        }
        return new byte[] {};
    }
}
