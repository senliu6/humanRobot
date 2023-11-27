package com.shciri.rosapp.utils.protocol;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReplyIPC {
    final byte SOF_DETECTED = 0;                /* 起始标志检测 */
    final byte HEADER_DETECTED_1 = 1;           /* 帧头检测 */
    final byte HEADER_DETECTED_2 = 2;           /* 帧头检测 */
    final byte HEADER_DETECTED_3 = 3;           /* 帧头检测 */
    final byte DATA_RECEIVED = 4;               /* 数据接收 */

    static byte unpack_step = 0;
    static int head_index = 0;
    static int tail_index = 0;
    static byte[] receiveBuffer = new byte[256];
    static byte[] unpack_buf = new byte[100];
    static int unpack_buf_idx = 0;
    static int unpack_len = 0;

    public BatteryReply batteryReply = new BatteryReply();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    public void ipc_put_rx_byte(byte[] data, int len) {
        lock.writeLock().lock();
        for (int i=0; i<len; i++) {
            receiveBuffer[head_index] = data[i];
            head_index = (head_index + 1) % 256;
        }
        lock.writeLock().unlock();
    }

    public short ipc_get_rx_byte() {
        short data = 0;
        if(tail_index != head_index)
        {
            lock.readLock().lock();
            data = receiveBuffer[tail_index];
            tail_index = (tail_index + 1) % 256;
            lock.readLock().unlock();
            return data;
        }else {
            return 0x1fff;
        }
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    public void getFullData() {
        short data = ipc_get_rx_byte();
        if (data == 0x1fff)
            return;

        switch (unpack_step){
            case SOF_DETECTED:
                if (data == Unpack.START_OF_FRAME_BYTE) {
                    unpack_buf_idx = 0;
                    unpack_buf[unpack_buf_idx] = (byte)data;

                    unpack_buf_idx ++;
                    unpack_step = HEADER_DETECTED_1;
                }
                break;

            case HEADER_DETECTED_1:
                unpack_buf[unpack_buf_idx] = (byte)data;
                unpack_len = data;

                unpack_step = HEADER_DETECTED_2;
                unpack_buf_idx++;
                break;

            case HEADER_DETECTED_2:
                unpack_buf[unpack_buf_idx] = (byte)data;
                unpack_len |= (((unpack_buf[unpack_buf_idx]) & 0x03) << 8);
                if((unpack_len) > 100)
                {
                    unpack_step = SOF_DETECTED;
                    unpack_buf_idx = 0;
                }
                else
                {
                    unpack_step = HEADER_DETECTED_3;
                    unpack_buf_idx++;
                }
                break;

            case HEADER_DETECTED_3:
                unpack_buf[unpack_buf_idx] = (byte)data;

                if((unpack_buf[3]) != ((unpack_buf[0]) + (unpack_buf[1]) + (unpack_buf[2])))
                {
                    unpack_step = SOF_DETECTED;
                    unpack_buf_idx = 0;
                } else {
                    unpack_step = DATA_RECEIVED;
                    unpack_buf_idx++;
                }
                break;

            case DATA_RECEIVED:
                if((unpack_buf_idx) < (unpack_len))
                {
                    unpack_buf[unpack_buf_idx] = (byte)data;
                    unpack_buf_idx++;
                }
                else if((unpack_buf_idx) > (unpack_len))
                {
                    unpack_step = SOF_DETECTED;
                    unpack_buf_idx = 0;
                }

                if (unpack_buf_idx == unpack_len)
                {
                    unpack_step = SOF_DETECTED;
                    int crc16 = CRC16.calc_crc16(unpack_buf, unpack_len - 2);
                    if (crc16 == (((unpack_buf[unpack_len - 1] << 8) | (unpack_buf[unpack_len - 2] & 0x00FF)) & 0xFFFF))
                    {
                        frame_received_handler_v1(unpack_buf);
                    }
                }
                break;
            default:
        }
    }

    private void frame_received_handler_v1(byte[] buf)
    {
        switch (buf[10]){
            case Unpack.BATTERY_REPLY_CMD:
                BatteryReply.current = (buf[12] << 8) | buf[11];
                BatteryReply.capacity_percent = buf[13];
                EventBus.getDefault().post(batteryReply);
                break;
        }
    }

    public static class BatteryReply {
        private static int current;  // unit:10mA
        private static short capacity_percent; // unit:%
        private static byte temperature;    // unit:degree centigrade
        private static int rated_capacitance; // unit:10mAh
        private static int surplus_capacitance; //unit:10mAh

        public int getCurrent(){
            return current;
        }

        public short getCapacity_percent(){
            return capacity_percent;
        }
    }
}
