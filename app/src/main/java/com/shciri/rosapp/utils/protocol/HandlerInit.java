package com.shciri.rosapp.utils.protocol;

public abstract class HandlerInit {
    public HeaderV1 header;
    public short crc16;

    public HandlerInit() {
        header = new HeaderV1();
        header.sof = Unpack.START_OF_FRAME_BYTE;

        header.checksum = (byte)(header.sof + header.ver_len);
        header.status = Unpack.FRAME_TYPE_CMD;
        header.sender = Unpack.DEVICE_IPC << 3 | Unpack.INDEX_IPC;

        /* 扩展修改点二： 上面的已固定不用更改，从这里开始，接收方可能会不同，需要根据命令调整 */
        header.receiver = Unpack.DEVICE_MCU << 3 | Unpack.INDEX_MCU;
        /* 命令对应指令集 */
        header.cmd_set = Unpack.IPC_CMD_SET;
    }

    public void handlerProcess(byte[] struct, int cmdLength) {
        header.ver_len = (short)(Unpack.FRAME_PROTOCOL_V1 << 10 | cmdLength);
        /* 具体的指令ID */
        header.cmd_id = Unpack.FAN_CONTROL_REQUEST_CMD;
        HeaderV1.headerTransfer(header, struct);
    }
}
