package com.shciri.rosapp.utils.protocol;

import org.jetbrains.annotations.NotNull;

public class HeaderV1 {
    public byte sof;
    public short ver_len;
    public byte checksum;
    public short seq;
    public byte status;
    public byte sender;
    public byte receiver;
    public byte cmd_set;
    public byte cmd_id;

    public void endowHeaderV1(byte[] buf) {
        sof = (byte)buf[0];
        ver_len = (short)(buf[1] | buf[2] << 10);
        checksum = (byte)buf[3];
        seq = (short)(buf[4] << 8 | buf[5]);
        status = (byte)buf[6];
        sender = (byte)buf[7];
        receiver = (byte)buf[8];
        cmd_set = (byte)buf[9];
        cmd_id = (byte)buf[10];
    }

    public int getReceiverDevice() {
        return (receiver & 0xF8) >> 3;
    }

    public int getReceiverIndex() {
        return (receiver & 0x07);
    }

    public void setEncrypt(boolean encrypt) {
        if(encrypt)
            status |= (1 << 4);
    }

    public void setSeq(short setSeq) {
        seq = setSeq;
    }

    public static void headerTransfer(HeaderV1 header, byte[] struct) {
        struct[0] = header.sof;
        struct[1] = (byte)(header.ver_len & 0x00FF);
        struct[2] = (byte)((header.ver_len & 0xFF00) >> 8);
        struct[3] = header.checksum;
        struct[4] = (byte)(header.seq & 0x00FF);
        struct[5] = (byte)((header.seq & 0xFF00) >> 8);
        struct[6] = header.status;
        struct[7] = header.sender;
        struct[8] = header.receiver;
        struct[9] = header.cmd_set;
        struct[10] = header.cmd_id;
    }

    public static int sizeof(@NotNull Class value) {

        if (value == byte.class) {
            return 1;
        }
        else if (value == short.class) {
            return 2;
        }
        else if (value == int.class) {
            return 4;
        }
        else if (value == float.class) {
            return 4;
        }
        else if (value == HeaderV1.class) {
            return  11;
        }

        return 0;
    }
}
