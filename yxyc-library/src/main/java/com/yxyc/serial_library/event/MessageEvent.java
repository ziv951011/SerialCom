package com.yxyc.serial_library.event;

public class MessageEvent {

    private int msg;
    private byte[] data;

    public MessageEvent(int msg, byte[] data) {
        this.msg = msg;
        this.data = data;
    }

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
