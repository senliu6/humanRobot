package com.shciri.rosapp.utils.protocol;

public class Unpack {
    /* 协议版本 */
public static final byte FRAME_PROTOCOL_V0                =           ((byte)0);
public static final byte FRAME_PROTOCOL_V1                =           ((byte)1);

/* 帧起始字节 */
public static final byte START_OF_FRAME_BYTE              =           ((byte)0xaa);

/* v1 协议下的接收地址 */;
public static final byte DEVICE_PC                        =           ((byte)0x00);
public static final byte INDEX_PC                         =           ((byte)0x00);

public static final byte DEVICE_GIMBAL                    =           ((byte)0x01);
public static final byte INDEX_IMU                        =           ((byte)0x00);
public static final byte INDEX_ESC                        =           ((byte)0x01);

public static final byte DEVICE_BLUETOOTH                 =           ((byte)0x02);
public static final byte INDEX_BLUETOOTH                  =           ((byte)0x00);

public static final byte DEVICE_PHONG                     =           ((byte)0x03);
public static final byte INDEX_PHONG                      =           ((byte)0x00);

public static final byte DEVICE_IPC                       =           ((byte)0x04);
public static final byte INDEX_IPC                        =           ((byte)0x00);

public static final byte DEVICE_MCU                       =           ((byte)0x05);
public static final byte INDEX_MCU                        =           ((byte)0x00);

/* 本机地址 */;
public static final byte LOCAL_DEVICE                     =            DEVICE_MCU;
public static final byte LOCAL_INDEX                      =            INDEX_MCU;

    /* v1 协议下的帧类型 */
public static final byte FRAME_TYPE_CMD                   =           ((byte)0x00);
public static final byte FRAME_TYPE_ACK                   =           ((byte)0x01);

/* * * * * * * * * * * * * * *
 * v1 协议下的工控机(IPC)指令集
 * * * * * * * * * * * * * * */
public static final byte IPC_CMD_SET                      =           ((byte)0x04);
public static final byte EMERGENCY_REQUEST_CMD            =           ((byte)0x00);
public static final byte FAN_CONTROL_REQUEST_CMD          =           ((byte)0x01);
public static final byte DIS_LED_CONTROL_REQUEST_CMD      =           ((byte)0x02);
}
